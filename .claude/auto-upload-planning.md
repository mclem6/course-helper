# Auto-Upload Syllabus Feature — Planning Summary

## Goal
Student uploads a syllabus PDF → app automatically creates their course, assignments, and schedule.

## Recommended Approach: Option 2 — Dedicated endpoint with confirmation step

### Backend
- New endpoint `POST /api/syllabus/parse`
- Reuse existing `PdfParser` to extract text
- Send text to GPT-4o with structured output prompt to extract: course name, assignments, due dates, exam dates
- Return preview JSON — do NOT save yet

### Frontend
- "Upload Syllabus" button (new)
- Review dialog showing extracted data — user can edit before confirming
- On confirm → call existing CRUD endpoints to save Course, Assignments, Events

### Why this approach
Syllabi are inconsistent — GPT-4o can misread dates or course codes. Confirmation step lets the student catch errors before data hits the DB. Backend work is minimal (PdfParser already exists); frontend dialog reuses existing form styling.

### Alternatives considered
- **Option 1** (no confirmation): faster to build but no safety net for bad GPT-4o output
- **Option 3** (extend existing agent with write tools): reuses agent infrastructure but adds unpredictable write behavior to a currently read-only agent
