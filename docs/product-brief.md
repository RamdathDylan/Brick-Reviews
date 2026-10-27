# Product Brief: Brick Reviews

|                        |               |
| ---------------------- | ------------- |
| **Author**       | Dylan Ramdath |
| **Last updated** | 2026-10-27    |
| **Version**      | 0.1           |

---

## 1. Summary

This application serves as a social platform where RIT verified students can rate and review courses and professors from the RIT catalog. While RateMyProfessors exists, utilizing verified RIT student emails ensures reviews come only from real students, with one review per student per course.

## 2. Problem

While utilizing RateMyProfessors students tend to add courses and professors that are not spelled properly or follow different formats. This leads to a difficult user experience when searching for a specific course or professor. Through using direct professor names and courses from the RIT catalog this pain point is minimized.

Additionally, anyone can post on RateMyProfessors without proving they are a student, and a single person can post multiple reviews. This makes it hard to trust the ratings. Requiring a verified RIT email and limiting each student to one review per course addresses this.

## 3. Target Users

| User       | Description                                            | Primary need                                                                                                                        |
| ---------- | ------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------- |
| Students   | Rate and review both professors and courses.           | Simplified user experience utilizing course catalog instead of user submitted course names and professor names.                     |
| Professors | Read feedback from students to improve course content (no account needed; read-only, like any visitor). | Simplified experience to view course feedback and student professor evaluations instead of using unverified user submitted courses. |

## 4. Goals

- Students can share honest feedback on courses that they have taken.
- Reviews only come from real students, so they can be trusted.
- Students can judge a course's difficulty and workload before registering.
- Professors can read verified student feedback.

## 5. Non-Goals (Out of Scope for v1)

- Comments or replies on ratings for a course/professor.
- Forum section where students can share materials to aid upcoming students in a course.
- Sorting/filter functionality for reviews.
- Professor accounts or professor replies to reviews.
- Official course descriptions or other content copied from RIT.
- Support for universities other than RIT.
- Native mobile app (responsive web only).

## 6. Core Features (MVP)

* **Account registration:** Users can sign up and log in using their RIT email address. After verifying their email, they can review courses.
* **Course search:** Users can search for a course by course name or code.
* **Professor search:** Users can search for a professor by name.
* **Add a course/professor review:** Users can add a review about a particular course tied to the professor that taught it.
* **Edit or delete review:** Users can edit or delete their own reviews.
* **Viewing reviews:** Anyone, including visitors without an account, can see the reviews and overall ratings for the courses and professors they search for.
* **Reporting:** Users can report a review to help moderate the website and keep the environment professional.
* **Admin moderation:** Admins can view reported reviews and decide whether to delete them. They can also ban users if necessary.
* **Course catalog:** Admins add courses (code and title) based on RIT's official course catalog.
* **Professor catalog:** Admins add professors (name and department) based on RIT's official faculty listings.

## 7. Constraints & Assumptions

**Constraints**

- **No affiliation with RIT.** This is an independent student project and is not affiliated with, endorsed by, or sponsored by RIT. The app will display a clear "Not affiliated with RIT" notice.
- **No RIT branding.** The app will not use RIT's name in its title, or its logos, colors, mascot artwork, or any other trademarks, and will not imply official status.
- **No scraping or automated access to rit.edu.** Per RIT's Terms of Use, site content may not be copied, republished, or publicly displayed.
- **Facts only, no copied content.** Courses and professors are stored as basic facts (course code, title, department, professor name), entered manually by admins. Official course descriptions and other RIT-authored text will not be copied or displayed.
- **No deep links to rit.edu.** RIT's Terms only permit a text link to the top-level page (www.rit.edu), so the app will not link to individual catalog or faculty pages.
- **No access to RIT systems or data.** The app does not integrate with RIT's systems or student records. Email verification only confirms that a user controls an @rit.edu address.
- **Reviews must focus on courses and teaching.** Content that is harassing, defamatory, or about a professor's personal life is prohibited and will be removed through moderation.
- **Reviewer anonymity.** Reviews are shown publicly as from a "Verified RIT student." User identities are stored only to enforce rules (one review per course, bans) and are never exposed through the API.
- **Minimal data collection.** Only the data needed to run the app is collected: email, password hash, and reviews. No unnecessary personal information.
- **Noncommercial.** The app will not be monetized and will not run ads.
- **Solo developer, part-time.** Scope is limited to what one student can build and maintain alongside classes and work.

**Assumptions**

- Course codes, course titles, and professor names are factual information and can be listed without copying RIT's content.
- RIT students have access to an @rit.edu email address and can receive verification emails at it.
- A third-party email service (e.g., SendGrid or Amazon SES) can deliver verification emails to @rit.edu addresses.
- Initial usage will be small (dozens to low hundreds of users), so a single deployed environment is sufficient.
- Users will report inappropriate reviews, and one admin can keep up with moderation at launch.
- If RIT raises a concern about the app, the project will respond promptly and make changes or take it down.

## 8. Security & Privacy Considerations

- **Passwords are never stored in plain text.** They are salted and hashed with Argon2id.
- **Only verified students can post.** Users must confirm ownership of an @rit.edu email before writing reviews.
- **Sessions expire.** Users are logged out automatically after a period of inactivity and immediately on logout.
- **Users can only change their own content.** Edit and delete actions are checked against the review's owner on the server.
- **Admin actions are restricted and logged.** Only admins can remove reviews or ban users, and every admin action is recorded.
- **Reviewer anonymity.** Reviews appear publicly as "Verified RIT student." The API never returns reviewers' emails or user IDs. Only admins can link a review to an account, and only for moderation.
- **Abuse prevention.** One review per user per course, rate limits on login and posting, and user reporting for inappropriate content.
- **Protection against common web attacks.** CSRF protection is enabled, the frontend and API are served from the same origin, and all user input is validated on the server.
- **Encrypted connections.** All traffic uses HTTPS. Secrets and database credentials are kept out of source code.
- **Minimal data.** The app stores only an email, a password hash, and reviews. See Constraints.
- **Data deletion (decision needed):** what happens to a user's reviews when they delete their account or are banned.

## 9. Risks & Open Questions

| Risk / Question | Impact | Mitigation / Next step |
|---|---|---|
| RIT objects to the app or claims trademark/terms violations | High | No RIT branding, "not affiliated" notice, facts only, no scraping; respond promptly and adjust or take down if contacted |
| Defamatory or harassing reviews about real, named professors | High | Content rules, user reporting, admin moderation, removal of personal attacks |
| Reviewer identity exposed (data leak or API bug) | High | Anonymity by design, DTOs that never return user info, security tests, Argon2id hashing |
| Fake or spam reviews skewing ratings | Medium | Email verification, one review per user per course, rate limiting |
| Verified students review courses they never took | Medium | Cannot be verified; rely on one review per course, user reporting, and admin moderation |
| Not enough reviews to be useful ("empty site" problem) | Medium | Seed popular SE/CS courses first; promote through clubs and student groups |
| Moderation load exceeds one admin | Medium | Start small; add more admins or automated filters if volume grows |
| Verification emails blocked or sent to spam by RIT's mail system | Medium | Test early with a real @rit.edu address; use a reputable email provider |
| Scope creep delays launch | Medium | Strict non-goals list; build Musts first |
| Limited time (solo, alongside school and work) | Medium | Vertical slices so there's always a working app; small MVP |
| Course and professor data goes out of date | Low | Admin tools to add, edit, and retire courses and professors |
| **Open:** What happens to reviews when a user is banned or deletes their account? | – | Decide before designing the schema |
| **Open:** Are reviews editable forever, or only for a time window? | – | Decide during user stories |

## 10. Tech Stack

- **Frontend:** Angular, TypeScript
- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, Spring Session (JDBC)
- **Database:** PostgreSQL, with Flyway for schema migrations
- **Authentication:** Server-side sessions (HttpOnly cookies), Argon2id password hashing, email verification
- **Testing:** JUnit, Mockito, Testcontainers, Playwright
- **Security tooling:** OWASP Dependency-Check, CodeQL, OWASP ZAP, Dependabot
- **Infrastructure / CI/CD:** Docker, Docker Compose (local development), GitHub Actions
- **Email delivery:** TBD (e.g., SendGrid or Amazon SES)
- **Hosting:** TBD (e.g., Render, Railway, or AWS)