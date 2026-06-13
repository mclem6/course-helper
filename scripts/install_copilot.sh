#!/usr/bin/env bash
set -euo pipefail

echo "This script installs the GitHub Copilot extensions using the 'code' CLI."
echo "Make sure the 'code' command is available (Command Palette → 'Shell Command: Install 'code' command')."

if ! command -v code >/dev/null 2>&1; then
  echo "Error: 'code' CLI not found. Install it from the Command Palette in VS Code." >&2
  exit 2
fi

echo "Installing GitHub Copilot (inline suggestions)..."
code --install-extension GitHub.copilot || echo "Warning: failed to install GitHub.copilot"

echo "Installing GitHub Copilot Chat (chat UI)..."
code --install-extension GitHub.copilot-chat || echo "Warning: failed to install GitHub.copilot-chat"

echo "Finished. Sign in to GitHub Copilot in VS Code and reload the window."
