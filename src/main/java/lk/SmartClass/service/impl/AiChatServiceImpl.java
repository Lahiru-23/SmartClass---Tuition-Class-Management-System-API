package lk.SmartClass.service.impl;

import lk.SmartClass.dto.response.AiChatResponse;
import lk.SmartClass.dto.response.ClassResponse;
import lk.SmartClass.dto.response.InvoiceResponse;
import lk.SmartClass.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService{

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final GuardianService guardianService;
    private final InvoiceService invoiceService;

    private final RestTemplate restTemplate = new RestTemplate();

    @org.springframework.beans.factory.annotation.Value("${ai.provider:local}")
    private String provider;

    @org.springframework.beans.factory.annotation.Value("${ai.api-key:}")
    private String apiKey;

    @org.springframework.beans.factory.annotation.Value("${ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.model:gpt-4o-mini}")
    private String model;

    @Override
    public AiChatResponse chat(String message, String username, List<String> roles) {
        String text = message == null ? "" : message.trim();
        boolean isStaff = roles != null && (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_TEACHER"));

        // 1) Try a precise, data-grounded local answer first — this covers the vast
        //    majority of real usage (counts, overdue fees, "how do I..." questions)
        //    and never depends on the network being available.
        String localReply = localAnswer(text, isStaff);
        if (localReply != null) {
            return AiChatResponse.builder().reply(localReply).source("local").build();
        }

        // 2) Otherwise, if an external LLM provider is configured, ask it for a
        //    general-purpose answer with a short system prompt for context.
        if ("openai".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isBlank()) {
            try {
                String llmReply = callExternalLlm(text, username, isStaff);
                if (llmReply != null && !llmReply.isBlank()) {
                    return AiChatResponse.builder().reply(llmReply).source("llm").build();
                }
            } catch (RestClientException ex) {
                log.warn("External AI provider call failed, falling back to local assistant: {}", ex.getMessage());
            }
        }

        // 3) Final fallback: friendly default local reply.
        return AiChatResponse.builder().reply(fallbackReply(isStaff)).source("local").build();
    }

    // ---------------------------------------------------------------- local engine

    private String localAnswer(String text, boolean isStaff) {
        String t = text.toLowerCase(Locale.ROOT);

        if (matchesAny(t, "hello", "hi ", "hi", "hey", "good morning", "good afternoon")) {
            return "Hello! I can tell you about students, teachers, classes, guardians and overdue fees, " +
                    "or walk you through how to use a feature. What would you like to know?";
        }

        if (matchesAny(t, "what can you do", "help", "capabilities", "what do you do")) {
            return "I can help with:\n" +
                    "• Live counts — students, teachers, classes, guardians\n" +
                    "• Overdue invoices and outstanding balances\n" +
                    "• Class capacity / enrollment status\n" +
                    "• Quick how-to guidance (adding a class, creating an invoice, marking attendance, etc.)\n" +
                    "Try asking something like \"how many students do we have?\" or \"any overdue invoices?\"";
        }

        if (!isStaff && matchesAny(t, "student", "teacher", "class", "guardian", "overdue", "invoice", "revenue")) {
            // Non-staff (STUDENT role) shouldn't get institution-wide stats — keep it general.
            return "I can help with general questions and how-to guidance, but detailed student/teacher/" +
                    "fee statistics are only available to admin and teacher accounts. Try asking me how to " +
                    "check your own fees, grades or attendance from the sidebar.";
        }

        if (isStaff && matchesAny(t, "how many student", "student count", "total student", "number of student")) {
            int count = safeCount(studentService::getAll);
            return "There are currently " + count + " student" + (count == 1 ? "" : "s") + " registered in SmartClass.";
        }

        if (isStaff && matchesAny(t, "how many teacher", "teacher count", "total teacher", "number of teacher")) {
            int count = safeCount(teacherService::getAll);
            return "There are currently " + count + " teacher" + (count == 1 ? "" : "s") + " on the platform.";
        }

        if (isStaff && matchesAny(t, "how many guardian", "guardian count", "total guardian")) {
            int count = safeCount(guardianService::getAll);
            return "There are currently " + count + " guardian" + (count == 1 ? "" : "s") + " linked to students.";
        }

        if (isStaff && matchesAny(t, "how many class", "class count", "total class", "number of class")) {
            try {
                List<ClassResponse> classes = classService.getAll();
                int totalEnrolled = classes.stream().mapToInt(ClassResponse::getEnrolledCount).sum();
                int totalCapacity = classes.stream().mapToInt(ClassResponse::getCapacity).sum();
                return "There are " + classes.size() + " classes running, with " + totalEnrolled +
                        " total enrolled seats out of " + totalCapacity + " capacity.";
            } catch (Exception ex) {
                return "I couldn't load the class list right now — please try again shortly.";
            }
        }

        if (isStaff && matchesAny(t, "overdue", "unpaid", "pending fee", "late payment", "who owes")) {
            try {
                List<InvoiceResponse> overdue = invoiceService.getOverdue();
                if (overdue.isEmpty()) {
                    return "Good news — there are no overdue invoices right now. 🎉";
                }
                BigDecimal totalBalance = overdue.stream()
                        .map(InvoiceResponse::getBalance)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                StringBuilder sb = new StringBuilder();
                sb.append("There ").append(overdue.size() == 1 ? "is " : "are ").append(overdue.size())
                        .append(" overdue invoice").append(overdue.size() == 1 ? "" : "s")
                        .append(", totalling Rs. ").append(totalBalance).append(" outstanding.\n\n");
                int shown = 0;
                for (InvoiceResponse inv : overdue) {
                    if (shown >= 5) {
                        sb.append("...and ").append(overdue.size() - shown).append(" more.");
                        break;
                    }
                    sb.append("• ").append(inv.getStudentName()).append(" — Rs. ").append(inv.getBalance())
                            .append(" (due ").append(inv.getDueDate()).append(")\n");
                    shown++;
                }
                return sb.toString().trim();
            } catch (Exception ex) {
                return "I couldn't load overdue invoices right now — please try again shortly.";
            }
        }

        if (matchesAny(t, "add a class", "create a class", "new class", "how do i add a class")) {
            return "To add a class: go to the Classes tab → fill in Subject ID, Teacher ID, Term ID, class name, " +
                    "capacity and room → click Add. You can Update or Delete a class by selecting it in the table first.";
        }

        if (matchesAny(t, "add a student", "create a student", "new student", "register a student")) {
            return "To add a student: open the Students tab → fill in the form (name, DOB, optional guardian and " +
                    "linked login account) → click Add. Click any row to edit it, or use the delete icon to remove it.";
        }

        if (matchesAny(t, "create an invoice", "add an invoice", "new invoice", "generate invoice")) {
            return "To create an invoice: open the Invoices tab → enter the Student ID, Fee Structure ID and Due " +
                    "Date → click Create Invoice. You can then look up a student's invoices by their ID below.";
        }

        if (matchesAny(t, "mark attendance", "attendance")) {
            return "Attendance is marked from the Teacher dashboard → Attendance tab: enter the Enrollment ID, " +
                    "pick the date and status (Present/Absent/Late), then submit.";
        }

        if (matchesAny(t, "reset password", "forgot password", "change password")) {
            return "There's no self-service password reset in this build yet — ask an admin to update your " +
                    "account, or use the Reset button on a form to simply clear it if you're mid-edit.";
        }

        if (matchesAny(t, "thank", "thanks", "thank you")) {
            return "You're welcome! Let me know if there's anything else I can help with.";
        }

        return null; // no confident local match — let the caller decide (LLM or generic fallback)
    }

    private String fallbackReply(boolean isStaff) {
        if (isStaff) {
            return "I'm not totally sure about that one yet. I'm best at questions about student/teacher/class " +
                    "counts, overdue fees, and how to use SmartClass features — try rephrasing, or ask \"what can you do?\"";
        }
        return "I'm not sure about that yet. I can help you understand how to check your fees, grades, attendance " +
                "or announcements — try asking \"what can you do?\"";
    }

    private boolean matchesAny(String haystack, String... needles) {
        for (String n : needles) {
            if (haystack.contains(n)) return true;
        }
        return false;
    }

    private int safeCount(java.util.function.Supplier<List<?>> supplier) {
        try {
            return supplier.get().size();
        } catch (Exception ex) {
            return 0;
        }
    }

    // ---------------------------------------------------------------- external LLM (optional)

    private String callExternalLlm(String userMessage, String username, boolean isStaff) {
        Map<String, Object> systemMsg = new LinkedHashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are the SmartClass Assistant, a helpful in-app AI for a tuition-class " +
                "management platform (students, teachers, classes, invoices, attendance). Keep answers short, " +
                "friendly and specific to school/tuition-class administration. The current user is '" + username +
                "' with " + (isStaff ? "staff (admin/teacher)" : "student") + " access.");

        Map<String, Object> userMsg = new LinkedHashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", List.of(systemMsg, userMsg));
        body.put("max_tokens", 300);
        body.put("temperature", 0.4);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
        if (response == null) return null;

        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) return null;

        Object first = choices.get(0);
        if (!(first instanceof Map<?, ?> firstMap)) return null;

        Object messageObj = firstMap.get("message");
        if (!(messageObj instanceof Map<?, ?> messageMap)) return null;

        Object content = messageMap.get("content");
        return content == null ? null : content.toString().trim();
    }
}
