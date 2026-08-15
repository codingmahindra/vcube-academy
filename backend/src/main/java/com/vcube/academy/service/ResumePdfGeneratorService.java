package com.vcube.academy.service;

import com.vcube.academy.dto.resume.ResumeVersionDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ResumePdfGeneratorService {

    public byte[] generatePdf(ResumeVersionDetailDto resume) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuilder textContent = new StringBuilder();

            textContent.append("=========================================================================\n");
            textContent.append(resume.getFullName().toUpperCase()).append("\n");
            textContent.append("Email: ").append(resume.getEmail());
            if (resume.getPhone() != null) textContent.append(" | Phone: ").append(resume.getPhone());
            if (resume.getLocation() != null) textContent.append(" | Location: ").append(resume.getLocation());
            textContent.append("\n");
            if (resume.getLinkedinUrl() != null) textContent.append("LinkedIn: ").append(resume.getLinkedinUrl()).append(" ");
            if (resume.getGithubUrl() != null) textContent.append("| GitHub: ").append(resume.getGithubUrl());
            textContent.append("\n=========================================================================\n\n");

            if (resume.getProfessionalSummary() != null && !resume.getProfessionalSummary().isBlank()) {
                textContent.append("PROFESSIONAL SUMMARY\n");
                textContent.append("-------------------------------------------------------------------------\n");
                textContent.append(resume.getProfessionalSummary()).append("\n\n");
            }

            if (resume.getTechnicalSkills() != null && !resume.getTechnicalSkills().isEmpty()) {
                textContent.append("TECHNICAL SKILLS\n");
                textContent.append("-------------------------------------------------------------------------\n");
                textContent.append(String.join(", ", resume.getTechnicalSkills())).append("\n\n");
            }

            if (resume.getExperiences() != null && !resume.getExperiences().isEmpty()) {
                textContent.append("PROFESSIONAL EXPERIENCE\n");
                textContent.append("-------------------------------------------------------------------------\n");
                resume.getExperiences().forEach(exp -> {
                    textContent.append(exp.getRoleTitle().toUpperCase()).append(" — ").append(exp.getCompanyName());
                    if (exp.getStartDate() != null) textContent.append(" (").append(exp.getStartDate()).append(" - ").append(exp.getEndDate() != null ? exp.getEndDate() : "Present").append(")");
                    textContent.append("\n");
                    if (exp.getDescription() != null) textContent.append(exp.getDescription()).append("\n");
                    if (exp.getBulletPoints() != null) {
                        exp.getBulletPoints().forEach(bp -> textContent.append("  • ").append(bp).append("\n"));
                    }
                    textContent.append("\n");
                });
            }

            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                textContent.append("KEY PROJECTS\n");
                textContent.append("-------------------------------------------------------------------------\n");
                resume.getProjects().forEach(p -> {
                    textContent.append(p.getTitle());
                    if (p.getTechStack() != null) textContent.append(" [").append(p.getTechStack()).append("]");
                    textContent.append("\n");
                    if (p.getDescription() != null) textContent.append(p.getDescription()).append("\n");
                    if (p.getBulletPoints() != null) {
                        p.getBulletPoints().forEach(bp -> textContent.append("  • ").append(bp).append("\n"));
                    }
                    textContent.append("\n");
                });
            }

            if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                textContent.append("EDUCATION\n");
                textContent.append("-------------------------------------------------------------------------\n");
                resume.getEducations().forEach(ed -> {
                    textContent.append(ed.getDegree()).append(" — ").append(ed.getInstitution());
                    if (ed.getStartYear() != null) textContent.append(" (").append(ed.getStartYear()).append(" - ").append(ed.getEndYear() != null ? ed.getEndYear() : "").append(")");
                    if (ed.getScoreOrCgpa() != null) textContent.append(" | CGPA/Score: ").append(ed.getScoreOrCgpa());
                    textContent.append("\n");
                });
                textContent.append("\n");
            }

            if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                textContent.append("CERTIFICATIONS\n");
                textContent.append("-------------------------------------------------------------------------\n");
                resume.getCertifications().forEach(c -> {
                    textContent.append("• ").append(c.getName());
                    if (c.getIssuingOrganization() != null) textContent.append(" (").append(c.getIssuingOrganization()).append(")");
                    if (c.getIssueDate() != null) textContent.append(" - ").append(c.getIssueDate());
                    textContent.append("\n");
                });
                textContent.append("\n");
            }

            // Create minimalist, standard PDF stream format
            String plainText = textContent.toString();
            String[] lines = plainText.split("\n");

            StringBuilder streamContent = new StringBuilder();
            streamContent.append("BT\n/F1 10 Tf\n40 780 Td\n13 TL\n");
            for (String line : lines) {
                String escaped = line.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
                streamContent.append("(").append(escaped).append(") '\n");
            }
            streamContent.append("ET\n");

            byte[] streamBytes = streamContent.toString().getBytes(StandardCharsets.ISO_8859_1);

            PrintWriter pw = new PrintWriter(baos, true, StandardCharsets.ISO_8859_1);
            pw.print("%PDF-1.4\n");
            pw.print("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
            pw.print("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
            pw.print("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> >>\nendobj\n");
            pw.print("4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
            pw.flush();

            baos.write(streamBytes);

            pw.print("\nendstream\nendobj\n");
            pw.print("xref\n0 5\n0000000000 65535 f \n0000000009 00000 n \n0000000058 00000 n \n0000000115 00000 n \n0000000280 00000 n \n");
            pw.print("trailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n400\n%%EOF\n");
            pw.flush();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF resume", e);
            return ("%PDF-1.4\n% VCUBE Academy ATS Resume\n" + resume.getFullName()).getBytes(StandardCharsets.UTF_8);
        }
    }
}
