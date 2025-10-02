# Day 9 — CI/CD Pipeline

**Deliverable:** Jenkins pipeline for automated build, test, and deployment.  
**Date:** 10/2/2025

---

## Goals
- Automate build & test process.  
- Package JAR and deploy it to Linux VM/WSL.  
- Run smoke test after deployment.  

---

## Files Added
- `Jenkinsfile` (repo root).  
- `scripts/smoke-test.sh`  
- `scripts/rollback.sh`  
- Systemd service file (`/etc/systemd/system/online-bookstore.service`) on Linux VM.  

---

## Jenkinsfile Stages
1. **Checkout** – Pull from GitHub.  
2. **Build & Test** – Run `mvn clean verify`.  
3. **Package** – Create shaded JAR (`-all.jar`).  
4. **Deploy to Linux VM** – Copy JAR to `/opt/bookstore/`.  
5. **Restart Service** – Restart `online-bookstore.service`.  
6. **Smoke Test** – Run full checkout flow, check for “Checkout complete”.  
7. **Rollback** – Available via `rollback.sh` if smoke test fails.  

---

## Deployment Setup
On Linux VM (WSL for this project):  
- App directory: `/opt/bookstore/`  
- Files: `online-bookstore-0.0.1-SNAPSHOT-all.jar`, `DynamoDBLocal.jar`, `DynamoDBLocal_lib/`  
- Managed by systemd service `online-bookstore.service`.

---

## Notes
- Integration tests skipped in Jenkins (no Docker).  
- Developers run ITs locally with DynamoDB Local JAR.  
- CD is simulated with WSL but works identically on a real Linux VM.


---

## 3. Day 10 — Project Report (not just another small doc)

This is the capstone deliverable — instead of a short “Day doc”, Day 10 is a consolidated project report covering Requirement → Design → Implementation → Testing → Deployment.

File: docs/PROJECT_REPORT.md (Day 10 deliverable).