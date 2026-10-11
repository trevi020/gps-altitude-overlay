#!/usr/bin/env bash
set -e
git add -A
git commit -m "WIP $(date +%F-%H%M)" || true
git push origin HEAD:wip/codespace-backup --force-with-lease
echo "Salvato su branch wip/codespace-backup"
