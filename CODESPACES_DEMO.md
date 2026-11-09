# GitHub Codespaces — Éclat demo

## Start the application
Run from the repository root:

```bash
bash ./.devcontainer/start-eclat.sh
```

The script starts Spring Boot in the background, checks that `/login` responds, saves the process ID, and writes logs to `logs/codespaces-app.log`. It does not terminate unknown processes occupying port 8080.

When running in Codespaces, the script prints the forwarded URL if Codespaces environment variables are available. Otherwise, open the **PORTS** panel and click the forwarded address for port 8080. The browser URL must be an `https://....app.github.dev` address; do not use `http://localhost:8080` on your computer.

The devcontainer configuration opens the browser automatically when port 8080 is forwarded and keeps the port private by default. To let a lecturer access the running demo, temporarily change port visibility to Public in the PORTS panel. Anyone with that public URL can access the app, so switch it back to Private after the demo.

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

The default SMS provider is `console`. The OTP is written to the Codespaces server log; no real SMS is sent. This is for coursework demonstration only, not production authentication.

For real SMS later, set `ECLAT_SMS_PROVIDER=notifylk` and `NOTIFYLK_API_KEY` as Codespaces secrets after verifying the Notify.lk account settings. Never commit API keys.
