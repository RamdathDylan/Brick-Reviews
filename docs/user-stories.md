# User Stories: Brick Reviews

|                        |               |
| ---------------------- | ------------- |
| **Author**       | Dylan Ramdath |
| **Last updated** | 2026-10-27    |
| **Version**      | 0.1           |

Related: [Product Brief](product-brief.md)

---

## How to read this document

**IDs**
- `EP-XX`: Epic (a group of related stories; roughly one vertical slice of the build)
- `US-XX`: User story
- `AC-XX`: Abuse case
- `F-XX`: Feature from the product brief (section 6), for traceability

**Priority (MoSCoW):** Must · Should · Could · Won't

**Story template**

```markdown
### US-XX: [Short title]
**As a** [user], **I want to** [action] **so that** [benefit].

**Acceptance criteria**
- Given [context], when [action], then [result].
- Given [context], when [action], then [result].

**Epic:** EP-XX · **Feature:** F-XX · **Priority:** Must
```

---

## Epic Overview

Epics are listed in build order.

| Epic | Description | Features (from brief) |
|---|---|---|
| EP-01: Authentication | Students create accounts, verify their RIT email, and log in/out | F-01 Account registration |
| EP-02: Catalog | Admins manage the courses and professors that can be reviewed | F-09 Course catalog, F-10 Professor catalog |
| EP-03: Search | Anyone can find courses and professors | F-02 Course search, F-03 Professor search |
| EP-04: Reviews | Students write and manage reviews; anyone can read them | F-04 Add review, F-05 Edit or delete review, F-06 Viewing reviews |
| EP-05: Moderation | Users report reviews; admins remove content and ban users | F-07 Reporting, F-08 Admin moderation |

---

## Decisions Needed Before Writing Stories


- **Banned or deleted users:** What happens to their reviews?
  - Decision: Reviews will be deleted from the database. 2026-10-27
- **Editing window:** Can reviews be edited forever, or only for a limited time?
  - Decision: Reviews can be edited forever. 2026-10-27
- **Review content:** What does a review contain? (e.g., rating scales, text length, term taken)
  - Decision: A review will contain a numbered score from 1-5 with increments of 1. Review text is limited to 2,000 characters, subject to change in the future. Reviews will be posted along with a date timestamp of when they were created. The term the course was taken is not collected; readers can judge how recent a review is from its date. 2026-10-27
- **Catalog:** How do courses and professors get into the app, and how are they linked?
  - Decision: Courses and professors are entered by admins from public RIT listings (facts only), individually or by CSV import. Students link a course and professor when writing a review. Students can request missing courses and professors for admin approval. Courses and professors are retired, never deleted, so their reviews remain. 2026-10-27
- **Admin accounts:** How does someone become an admin?
  - Decision: Admin accounts are created manually in the database (through a migration or SQL), not through the app. There is no in-app way to become an admin. 2026-10-27

---

## EP-01: Authentication

### US-01: Sign up
**As a** student, **I want to** sign up for an account with my RIT email **so that** I can leave reviews on courses I have taken.

**Acceptance criteria**
- Given a user enters a valid RIT email and a password that meets requirements, when they click submit, then an unverified account is created, a verification email is sent, and they see a notification saying they will receive an email to verify their email address.
- Given a user enters an RIT email that already has an account, when they click submit, then they see the same notification as a new signup (so the page does not reveal whether an account exists), and an email is sent to that address letting the owner know an account already exists.
- Given a user is entering a password for signup and the password does not meet requirements (at least 12 characters and not a commonly used password), when they click submit, then they should be shown an error message saying that the password does not meet security requirements.
- Given a user is entering a password for signup and they have not confirmed the password, when they click submit, then they should be shown an error message saying that the passwords do not match.
- Given a user has entered an email that is not an RIT email, when they click submit, then they should see an error message that says they need to enter an RIT email.

**Epic:** EP-01 · **Feature:** F-01 · **Priority:** Must

### US-02: Email verification
**As a** student, **I want to** verify my email after sign up **so that** only I can create an account using my email.

**Acceptance criteria**
- Given a user has received the email, when they click on the link, then their account is marked as verified and they are routed to a success page.
- Given a user does not use the link within 24 hours, when they try to click the link, then they should be routed to a failure page explaining that the link expired, with an option to resend the verification email.
- Given a user has already used the verification link, when they click it again, then they should be routed to a page explaining that the link is no longer valid.
- Given a verification link is invalid or has been tampered with, when it is opened, then the user is routed to a failure page and no account is changed.
- Given a user requests a new verification email, when they submit their email, then a new link is sent, any previous link stops working, and they see the same notification whether or not the account exists.

**Epic:** EP-01 · **Feature:** F-01 · **Priority:** Must

### US-03: Log In
**As a** student, **I want to** log in **so that** I can post reviews.

**Acceptance criteria**
- Given a user attempts to log in and doesn't have an account or enters their information incorrectly, when they click log in, then they should see an error message saying that the account was not found or the information is incorrect.
- Given a user enters the correct email and password but hasn't verified their account, when they click log in, then they are not logged in and see a message asking them to verify their email, with an option to resend the verification email.
- Given a user attempts to log in and they've entered their information correctly and the account is verified, when they click log in, then they should be redirected to the regular website and the login button is a profile button instead.

**Epic:** EP-01 · **Feature:** F-01 · **Priority:** Must

### US-04: Log Out
**As a** student, **I want to** log out **so that** my account is safe from users of my laptop.

**Acceptance criteria**
- Given a user is logged in, when they click log out, then they should be redirected to the search page and be logged out of their account.
- Given a user has logged out, when they use the browser back button or reuse their old session cookie, then they are not logged back in because the session has been invalidated on the server.

**Epic:** EP-01 · **Feature:** F-01 · **Priority:** Must

### US-23: Delete my account
**As a** student, **I want to** delete my account **so that** my information and reviews are removed if I no longer want to use the site.

**Acceptance criteria**
- Given a logged-in student chooses to delete their account, when they re-enter their password and confirm, then their account and all of their reviews are deleted from the database, the overall ratings update, and they are logged out.
- Given a student is deleting their account, when they enter the wrong password, then nothing is deleted and they are shown an error message.
- Given a student has deleted their account, when they sign up again with the same RIT email, then they can create a new account.
- Given a banned user, when they try to delete their account, then the request is rejected so the ban cannot be bypassed.

**Epic:** EP-01 · **Feature:** F-01 · **Priority:** Should

---

## EP-02: Catalog

### US-05: Add a course
**As an** admin, **I want to** add a course to the database **so that** students can leave reviews on the course.

**Acceptance criteria**
- Given an admin enters a course code, title, and department, when they click submit, then the course is saved and appears in course search.
- Given an admin leaves any field blank, when they click submit, then an error message is shown saying that all fields must be filled out and nothing is saved.
- Given an admin enters a course code that does not match the standard format (e.g., `SWEN-261`), when they click submit, then an error message explains the required format and nothing is saved.
- Given an admin enters a course code that already exists, when they click submit, then an error message says the course already exists and nothing is saved.
- Given a user who is not an admin, when they try to add a course, then the request is rejected (403) and nothing is saved.

**Epic:** EP-02 · **Feature:** F-09 · **Priority:** Must

### US-06: Edit or retire a course
**As an** admin, **I want to** edit and retire courses **so that** I can fix mistakes, update course codes, and retire old courses.

**Acceptance criteria**
- Given an admin searches for a course, when they select it, then they can edit its code, title, and department.
- Given an admin has edited a course, when they submit, then they are asked to confirm the changes, and once confirmed the course is updated and its existing reviews stay linked to it.
- Given an admin is editing a course and leaves any field blank or enters an invalid or duplicate course code, when they click submit, then an error message is shown and nothing is changed.
- Given an admin retires a course, when they confirm, then the course can no longer be selected for new reviews, but its existing reviews remain visible.
- Given an admin views a retired course, when they choose to reactivate it, then it can be selected for new reviews again.
- Given a user who is not an admin, when they try to edit or retire a course, then the request is rejected (403) and nothing is changed.

**Epic:** EP-02 · **Feature:** F-09 · **Priority:** Must

### US-07: Add a professor
**As an** admin, **I want to** add a professor to the database **so that** students can choose them as the professor who taught a course.

**Acceptance criteria**
- Given an admin enters a professor's name and department, when they click submit, then the professor is saved and appears in professor search.
- Given an admin leaves any field blank, when they click submit, then an error message is shown saying that all fields must be filled out and nothing is saved.
- Given an admin enters a name and department that match an existing professor, when they click submit, then a warning says a matching professor already exists, and the admin can cancel or confirm that this is a different person.
- Given a user who is not an admin, when they try to add a professor, then the request is rejected (403) and nothing is saved.

**Epic:** EP-02 · **Feature:** F-10 · **Priority:** Must

### US-08: Edit or retire a professor
**As an** admin, **I want to** edit and retire professors **so that** I can fix mistakes, update departments, and retire professors who no longer teach.

**Acceptance criteria**
- Given an admin searches for a professor, when they select them, then they can edit the professor's name and department.
- Given an admin has edited a professor, when they submit, then they are asked to confirm the changes, and once confirmed the professor is updated and their existing reviews stay linked to them.
- Given an admin is editing a professor and leaves any field blank, when they click submit, then an error message is shown and nothing is changed.
- Given an admin retires a professor, when they confirm, then the professor can no longer be selected for new reviews, but their existing reviews remain visible.
- Given an admin views a retired professor, when they choose to reactivate them, then they can be selected for new reviews again.
- Given a user who is not an admin, when they try to edit or retire a professor, then the request is rejected (403) and nothing is changed.

**Epic:** EP-02 · **Feature:** F-10 · **Priority:** Must

### US-09: Bulk import courses and professors
**As an** admin, **I want to** upload a CSV file of courses or professors **so that** I can add many entries at once instead of one by one.

**Acceptance criteria**
- Given an admin uploads a valid CSV of courses (code, title, department) or professors (name, department), when the import finishes, then all valid rows are saved and the admin sees a summary of how many were added.
- Given a CSV contains rows with missing fields or invalid course codes, when it is imported, then those rows are skipped and listed in the summary with the reason, and all valid rows are still saved.
- Given a CSV contains entries that already exist, when it is imported, then the duplicates are skipped and listed in the summary.
- Given an admin uploads a file that is not a valid CSV or has the wrong columns, when they submit, then an error message is shown and nothing is saved.
- Given a user who is not an admin, when they try to import a file, then the request is rejected (403) and nothing is saved.

**Epic:** EP-02 · **Feature:** F-09, F-10 · **Priority:** Should

### US-10: Request a missing course or professor
**As a** student, **I want to** request a course or professor that isn't listed **so that** I can review the class I actually took.

**Acceptance criteria**
- Given a verified student can't find a course, when they submit a course code, title, and department, then a pending request is created and they see a message that it will be reviewed.
- Given a verified student can't find a professor, when they submit a name and department, then a pending request is created and they see a message that it will be reviewed.
- Given a student submits a request with missing fields or an invalid course code, when they click submit, then an error message is shown and no request is created.
- Given the course or professor already exists, when a student submits a request for it, then they are told it already exists and shown the existing entry.
- Given a user who is not logged in or not verified, when they try to submit a request, then they are asked to log in or verify their email first.

**Epic:** EP-02 · **Feature:** F-09, F-10 · **Priority:** Should

### US-11: Approve or reject requests
**As an** admin, **I want to** review requested courses and professors **so that** only accurate entries are added to the catalog.

**Acceptance criteria**
- Given there are pending requests, when an admin opens the request list, then they see each request's details.
- Given an admin approves a request, when they confirm (optionally correcting any details), then the course or professor is added to the catalog and the request is closed.
- Given an admin rejects a request, when they confirm, then nothing is added and the request is closed.
- Given a user who is not an admin, when they try to view, approve, or reject requests, then the request is rejected (403).

**Epic:** EP-02 · **Feature:** F-09, F-10 · **Priority:** Should

---

## EP-03: Search

### US-12: Search professors
**As a** user, **I want to** search by professors **so that** I can see reviews of a certain professor.

**Acceptance criteria**
- Given any user enters search terms for a professor, when they click enter, then they should be shown a list of matching professors with their department, and selecting one opens that professor's page.
- Given any user enters part of a professor's name in any capitalization (e.g., "smi"), when they click enter, then all professors whose names contain those terms are shown (e.g., "Smith").
- Given any user enters search terms that don't match any professor, when they click enter, then they should be shown a message informing them that no professor was found by that name.
- Given any user submits an empty search, when they click enter, then no search is run and they are prompted to enter a name.
- Given a visitor who is not logged in, when they search for a professor, then results are shown.
- Given a professor has been retired, when their name matches a search, then they are still shown in the results and marked as retired.

**Epic:** EP-03 · **Feature:** F-03 · **Priority:** Must

### US-13: Search courses
**As a** user, **I want to** search by courses **so that** I can see reviews pertaining to a class.

**Acceptance criteria**
- Given any user enters search terms for a course, when they click enter, then they should be shown a list of matching courses with their code and title, and selecting one opens that course's page.
- Given any user enters part of a course code or title in any capitalization, when they click enter, then all courses whose code or title contains those terms are shown.
- Given any user enters a course code in a different format (e.g., "swen261", "SWEN 261", or "swen-261"), when they click enter, then the matching course (e.g., SWEN-261) is shown.
- Given any user enters search terms that don't match any course, when they click enter, then they should be shown a message informing them that no course was found by that name.
- Given any user submits an empty search, when they click enter, then no search is run and they are prompted to enter a course code or title.
- Given a visitor who is not logged in, when they search for a course, then results are shown.
- Given a course has been retired, when it matches a search, then it is still shown in the results and marked as retired.

**Epic:** EP-03 · **Feature:** F-02 · **Priority:** Must

---

## EP-04: Reviews

### US-14: Add a review
**As a** student, **I want to** review a course **so that** I can share my experience in that course with a specific professor.

**Acceptance criteria**
- Given a verified student is adding a review, when they choose a course and a professor, add a score from 1 to 5, write a review, and submit, then the review is saved with the date it was created and appears on that course's page and that professor's page.
- Given a student is adding a review, when they are entering the required fields and they leave one blank, then they will be shown an error message saying that they must fill out all fields.
- Given a student enters a score that is not a whole number from 1 to 5, or review text longer than the maximum length, when they submit, then they are shown an error message and the review is not saved.
- Given a student is trying to add a review for a course they have already reviewed, when they attempt to submit their review, then they will be shown an error message saying that they have already added a review for this course.
- Given a student is choosing a course or professor, when the list is shown, then retired courses and professors cannot be selected.
- Given a user who is not logged in or has not verified their email, when they try to add a review, then they are asked to log in or verify their email first and no review is saved.
- Given a review has been posted, when anyone views it, then it is shown as from a "Verified RIT student" and never shows the reviewer's name or email.

**Epic:** EP-04 · **Feature:** F-04 · **Priority:** Must

### US-15: Edit own review
**As a** student, **I want to** edit my reviews **so that** I can change my review in the event my opinions change.

**Acceptance criteria**
- Given a student is editing their own review, when they edit the fields they wish to and submit their review, then the review is updated, the date it was last edited is recorded, and the updated review is shown on the course and professor pages.
- Given a student is editing a review, when they are editing the fields and they leave a field blank, then they should be shown an error message saying that all fields must be filled in.
- Given a student is editing a review, when they enter an invalid score or text that is too long, then they are shown an error message and the review is not changed.
- Given a student tries to edit a review that is not theirs, when they submit the edit, then the request is rejected (403) and the review is not changed.

**Epic:** EP-04 · **Feature:** F-05 · **Priority:** Must

### US-16: Delete own review
**As a** student, **I want to** delete my review **so that** I don't leave reviews of mine that are no longer relevant.

**Acceptance criteria**
- Given a student is viewing their own review, when they choose to delete it, then they are asked to confirm before anything is deleted.
- Given a student confirms deleting their review, when the deletion completes, then the review is removed from the database, no longer appears on the course or professor pages, and the overall ratings update.
- Given a student has deleted their review for a course, when they want to review that course again, then they are able to add a new review.
- Given a student tries to delete a review that is not theirs, when they submit the request, then it is rejected (403) and the review is not deleted.

**Epic:** EP-04 · **Feature:** F-05 · **Priority:** Must

### US-17: View reviews
**As a** user, **I want to** see the reviews and overall rating for a course or professor **so that** I can decide which classes to take and which professors to take them with.

**Acceptance criteria**
- Given any user opens a course's page, when it loads, then they see the course's average score, the number of reviews, and each review with its score, text, professor, and date.
- Given any user opens a professor's page, when it loads, then they see the professor's average score, the number of reviews, and each review with its score, text, course, and date.
- Given a course or professor has no reviews yet, when its page loads, then a message says there are no reviews yet instead of an average score.
- Given a visitor who is not logged in, when they open a course or professor page, then the reviews are shown.
- Given a page has many reviews, when it loads, then reviews are shown newest first and split into pages.
- Given a review contains HTML or script code, when it is displayed, then it is shown as plain text and never run.

**Epic:** EP-04 · **Feature:** F-06 · **Priority:** Must

---

## EP-05: Moderation

### US-18: Report a review
**As a** student, **I want to** report a review **so that** inappropriate content can be removed and the environment stays professional.

**Acceptance criteria**
- Given a verified student is viewing a review, when they report it and choose a reason (e.g., harassment, personal attack, spam, not about the course), then a report is created and they see a message thanking them for the report.
- Given a student has already reported a review, when they try to report it again, then they are told they have already reported it and no new report is created.
- Given a user who is not logged in or has not verified their email, when they try to report a review, then they are asked to log in or verify their email first.
- Given a review has been reported, when other users view it, then it stays visible until an admin decides on it, and the reporter's identity is never shown to the reviewer or other users.

**Epic:** EP-05 · **Feature:** F-07 · **Priority:** Must

### US-19: View reported reviews
**As an** admin, **I want to** see all reported reviews in one place **so that** I can decide what to do with each one.

**Acceptance criteria**
- Given there are open reports, when an admin opens the moderation queue, then they see each reported review with its text, course, professor, the number of reports, and the reasons given.
- Given a review has been reported by several users, when it appears in the queue, then it is shown once with all of its reports grouped together.
- Given there are no open reports, when an admin opens the queue, then a message says there is nothing to review.
- Given an admin is looking at a reported review, when they view its details, then they can see which account wrote it, for moderation only.
- Given a user who is not an admin, when they try to view the moderation queue, then the request is rejected (403).

**Epic:** EP-05 · **Feature:** F-08 · **Priority:** Must

### US-20: Remove a reported review
**As an** admin, **I want to** remove a review that breaks the content rules **so that** harmful content is taken off the site.

**Acceptance criteria**
- Given an admin is viewing a reported review, when they choose to remove it and confirm, then the review is deleted, no longer appears on the course or professor pages, the overall ratings update, and all of its reports are closed.
- Given an admin removes a review, when the removal completes, then the action is recorded in the admin log with which admin did it, when, and why.
- Given a user who is not an admin, when they try to remove a review through moderation, then the request is rejected (403) and the review is not changed.

**Epic:** EP-05 · **Feature:** F-08 · **Priority:** Must

### US-21: Dismiss a report
**As an** admin, **I want to** dismiss reports on reviews that don't break the rules **so that** fair reviews stay up and the queue stays clear.

**Acceptance criteria**
- Given an admin decides a reported review is acceptable, when they dismiss the report and confirm, then the review stays visible and all of its open reports are closed.
- Given an admin dismisses a report, when the dismissal completes, then the action is recorded in the admin log.
- Given a user who is not an admin, when they try to dismiss a report, then the request is rejected (403).

**Epic:** EP-05 · **Feature:** F-08 · **Priority:** Must

### US-22: Ban a user
**As an** admin, **I want to** ban a user who repeatedly breaks the rules **so that** they can no longer post harmful content.

**Acceptance criteria**
- Given an admin is viewing a reported review, when they choose to ban its author and confirm, then the author's account is disabled and all of their reviews are deleted from the database.
- Given a user has been banned, when they are logged in at the time of the ban, then their session ends immediately.
- Given a user has been banned, when they try to log in with the correct email and password, then they see a message that their account has been suspended and they are not logged in. With an incorrect password, they see the same error as any failed login, so the page does not reveal that the account exists.
- Given a user has been banned, when they try to sign up again with the same RIT email, then no new account is created.
- Given an admin bans a user, when the ban completes, then the action is recorded in the admin log with which admin did it, when, and why.
- Given an admin tries to ban themselves or another admin, when they submit, then the request is rejected.
- Given a user who is not an admin, when they try to ban a user, then the request is rejected (403).

**Epic:** EP-05 · **Feature:** F-08 · **Priority:** Should

---

## Abuse Cases
<!-- Written from an attacker's point of view. Each "Expected defense" becomes a security test. -->

### AC-01: Check whether a student has an account
**As an** attacker, **I want to** enter a specific student's RIT email on the signup or resend page **so that** I can find out whether they use the app.

**Expected defense:** The page shows the same message whether or not an account exists. Only the email owner is told that an account already exists.

**Related stories:** US-01, US-02

### AC-02: Guess passwords
**As an** attacker, **I want to** try many passwords against one account **so that** I can break into it.

**Expected defense:** Login attempts are rate limited, and repeated failures temporarily block further attempts.

**Related stories:** US-03

### AC-03: Use admin functions as a regular user
**As an** attacker with a regular account, **I want to** call the admin endpoints directly (bypassing the UI) **so that** I can add, edit, or retire courses and professors.

**Expected defense:** Every admin endpoint checks the user's role on the server and returns 403 for non-admins; nothing is changed.

**Related stories:** US-05, US-06, US-07, US-08, US-09, US-11

### AC-04: Attack the search box
**As an** attacker, **I want to** enter malicious input (such as SQL injection) or flood the search with requests **so that** I can read or damage the database or slow the site down.

**Expected defense:** Searches use parameterized queries, input has a maximum length, and search requests are rate limited. Malicious input is treated as plain text and returns no results.

**Related stories:** US-12, US-13

### AC-05: Edit or delete someone else's review
**As an** attacker, **I want to** change the review ID in an edit or delete request **so that** I can alter or remove another student's review.

**Expected defense:** The server checks that the logged-in user owns the review before any change and returns 403 otherwise; the review is unchanged.

**Related stories:** US-15, US-16

### AC-06: Find out who wrote a review
**As an** attacker, **I want to** inspect the API responses for reviews **so that** I can discover the reviewer's identity.

**Expected defense:** Review responses never include the reviewer's name, email, or user ID. Only admins can link a review to an account, and only for moderation.

**Related stories:** US-14, US-17

### AC-07: Inject a script into a review
**As an** attacker, **I want to** write a review containing a script **so that** it runs in other users' browsers when they read it.

**Expected defense:** Review text is always displayed as plain text (Angular's default escaping, no raw HTML), so the script is shown but never run.

**Related stories:** US-14, US-15, US-17

### AC-08: Post many reviews for one course
**As an** attacker, **I want to** call the review API directly several times **so that** I can post multiple reviews for the same course and skew its rating.

**Expected defense:** The database enforces one review per user per course with a unique constraint, review posting is rate limited, and extra attempts are rejected.

**Related stories:** US-14

### AC-09: Abuse the report button
**As an** attacker, **I want to** report a fair review many times **so that** it gets removed or the moderation queue gets flooded.

**Expected defense:** Only verified students can report, each user can report a review only once, reporting is rate limited, and a review is never removed automatically; an admin always decides.

**Related stories:** US-18, US-19

### AC-10: Return after a ban
**As a** banned user, **I want to** keep using my old session or sign up again **so that** I can continue posting.

**Expected defense:** Banning ends all of the user's sessions on the server, banned accounts cannot log in, and the same RIT email cannot be used to create a new account.

**Related stories:** US-22