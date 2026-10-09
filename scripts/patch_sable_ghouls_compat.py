#!/usr/bin/env python3
"""Apply the narrow Create 0.5.x compatibility adaptation to the pinned Sable Forge source."""
import json
import sys
from pathlib import Path

root = Path(sys.argv[1])
props = root / "gradle.properties"
text = props.read_text(encoding="utf-8")
old_version = "version=2.0.5-port.1"
new_version = "version=2.0.5-port.2"
if text.count(old_version) != 1:
    raise SystemExit("Pinned Sable version marker changed; refusing an unreviewed patch")
props.write_text(text.replace(old_version, new_version), encoding="utf-8")

build = root / "forge/build.gradle"
text = build.read_text(encoding="utf-8")
old_range = "create_version_range: '[6.0.8,6.1.0)'"
new_range = "create_version_range: '[0,)'"
if text.count(old_range) != 1:
    raise SystemExit("Pinned Create version range changed; refusing an unreviewed patch")
build.write_text(text.replace(old_range, new_range), encoding="utf-8")

tools_build = root / "tools/build.ps1"
text = tools_build.read_text(encoding="utf-8")
old_tools_setting = "$includeTestTools = $Mode -in @('test', 'quick')"
new_tools_setting = "$includeTestTools = $Mode -eq 'test'"
if text.count(old_tools_setting) != 1:
    raise SystemExit("Pinned Sable quick-mode test-tools marker changed; refusing an unreviewed patch")
tools_build.write_text(text.replace(old_tools_setting, new_tools_setting), encoding="utf-8")

config = root / "forge/src/port/resources/sable-create.mixins.json"
data = json.loads(config.read_text(encoding="utf-8"))
if not data.get("mixins") or not data.get("client"):
    raise SystemExit("Expected the pinned Create/Flywheel mixin set; refusing an unreviewed patch")
data["mixins"] = []
data["client"] = []
config.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
print("Patched Sable to accept Create versions, skip Create/Flywheel 1.0 mixins, and keep quick mode out of test-tools packaging.")
