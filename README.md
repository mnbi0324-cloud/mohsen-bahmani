# مدیریت معدن | مهندس بهمنی
Android native starter project (offline-first).

## Build
GitHub Actions: Actions -> Build Android APK -> Run workflow.
Artifact: MineLoad-debug-apk.

## First login
username: admin
password: admin123
Change the initial password before using with real data. User creation is currently admin-only.

## Modules in this starter
Work log, staff, fuel, expenses, samples, reports, users. Data stored locally in SQLite.
This is a starter build, not production-ready: cloud sync, photo capture, GPS integration, signatures, PDF/Excel, Persian date conversion, role/permission granularity, password reset and admin password change are planned follow-up modules. Never store sensitive mine records in this build until those protections are implemented and tested.
