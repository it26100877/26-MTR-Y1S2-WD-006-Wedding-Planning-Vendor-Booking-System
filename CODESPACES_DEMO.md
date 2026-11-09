# GitHub Codespaces — Éclat demo

## Start the application
From the repository root, run:

```bash
chmod +x .devcontainer/start-eclat.sh
./.devcontainer/start-eclat.sh
```

The helper starts Spring Boot in the background, waits for `/login` to respond, records the PID, and writes logs to `logs/codespaces-app.log`. It refuses to kill an unrelated process already using port 8080.

Open the **PORTS** panel and use the forwarded URL for port 8080. Do not use the computer's `localhost:8080` address for a cloud Codespace.

## Check status and logs
```bash
curl -I http://127.0.0.1:8080/login
tail -n 100 logs/codespaces-app.log
```

## Stop the app
```bash
if [ -f .eclat-app.pid ]; then kill "$(cat .eclat-app.pid)" && rm -f .eclat-app.pid; fi
```

## Customer OTP for demo
The default demo SMS provider is `console`. A customer OTP is written to the Codespaces terminal log. No real SMS is sent. Never make the application or logs public while using real customer data or credentials.

For real SMS later, set `ECLAT_SMS_PROVIDER=notifylk` and `NOTIFYLK_API_KEY` as Codespaces secrets after verifying the Notify.lk account configuration. Do not commit secrets.
