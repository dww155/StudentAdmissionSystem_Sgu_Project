package com.sgu.student_admission_system.webController;

import com.sgu.student_admission_system.entity.AdmissionPreference;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.repository.AdmissionPreferenceRepository;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginController {
    DateTimeFormatter BIRTH_INPUT_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    ApplicantRepository applicantRepository;
    AdmissionPreferenceRepository admissionPreferenceRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("student", Map.of("cccd", "", "birth", ""));
        return "login";
    }

    @GetMapping("/result")
    public String result(Model model) {
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", null);
        }
        return "result";
    }

    @PostMapping("/login")
    public String check(
            @RequestParam("cccd") String cccd,
            @RequestParam("birth") String birth,
            Model model
    ) {
        Applicant applicant = applicantRepository.findByCccd(cccd).orElse(null);
        if (applicant == null || !isBirthMatched(applicant.getDateOfBirth(), birth)) {
            model.addAttribute("error", "CCCD hoac ngay sinh khong dung.");
            model.addAttribute("student", Map.of(
                    "cccd", cccd == null ? "" : cccd,
                    "birth", birth == null ? "" : birth
            ));
            return "login";
        }

        List<AdmissionPreference> preferences = admissionPreferenceRepository
                .findAllByApplicant_CccdIn(List.of(cccd))
                .stream()
                .sorted(Comparator.comparing(AdmissionPreference::getPriorityOrder))
                .toList();

        List<Map<String, Object>> admissionResults = preferences.stream()
                .map(this::toAdmissionResultRow)
                .toList();

        List<Map<String, Object>> scoreDetails = preferences.stream()
                .map(this::toScoreDetailRow)
                .toList();

        model.addAttribute("student", Map.of(
                "cccd", applicant.getCccd(),
                "birth", applicant.getDateOfBirth() == null ? "" : applicant.getDateOfBirth().format(BIRTH_INPUT_FORMATTER)
        ));
        model.addAttribute("statusSummary", admissionResults.isEmpty() ? "Chua co du lieu xet tuyen" : "Cap nhat moi nhat");
        model.addAttribute("admissionResults", admissionResults);
        model.addAttribute("scoreDetails", scoreDetails);
        return "result";
    }

    private boolean isBirthMatched(LocalDate dateOfBirth, String birthInput) {
        if (dateOfBirth == null || birthInput == null || birthInput.isBlank()) {
            return false;
        }
        try {
            LocalDate parsed = LocalDate.parse(birthInput.trim(), BIRTH_INPUT_FORMATTER);
            return dateOfBirth.equals(parsed);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private Map<String, Object> toAdmissionResultRow(AdmissionPreference preference) {
        String majorName = preference.getMajor() == null ? "" : preference.getMajor().getMajorName();
        String cutoff = preference.getMajor() == null || preference.getMajor().getAdmissionScore() == null
                ? "-"
                : preference.getMajor().getAdmissionScore().toPlainString();

        return Map.of(
                "preference", "NV" + safeInt(preference.getPriorityOrder()),
                "major", majorName,
                "method", safeText(preference.getMethod()),
                "combo", safeText(preference.getSubjectGroup()),
                "score", safeDecimal(preference.getAdmissionScore()),
                "cutoff", cutoff,
                "status", safeText(preference.getResult())
        );
    }

    private Map<String, Object> toScoreDetailRow(AdmissionPreference preference) {
        return Map.of(
                "preference", "NV" + safeInt(preference.getPriorityOrder()),
                "method", safeText(preference.getMethod()),
                "combo", safeText(preference.getSubjectGroup()),
                "subjectScores", "-",
                "bonus", safeDecimal(preference.getBonusScore()),
                "total", safeDecimal(preference.getAdmissionScore())
        );
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String safeDecimal(BigDecimal value) {
        return value == null ? "-" : value.toPlainString();
    }
}
