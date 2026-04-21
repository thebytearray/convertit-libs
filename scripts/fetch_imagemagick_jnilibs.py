#!/usr/bin/env python3
"""Resolve latest ImageMagick-Android zips; print TSV lines: abi<TAB>url. Exits 1 on error."""
import json
import os
import sys
import urllib.request

API = os.environ.get(
    "IMAGEMAGICK_RELEASE_API_URL",
    "https://api.github.com/repos/codewithtamim/Android-ImageMagick7/releases/latest",
)
req = urllib.request.Request(
    API,
    headers={
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
    },
)
with urllib.request.urlopen(req) as r:
    data = json.load(r)

zips = [a for a in data.get("assets", []) if a.get("name", "").endswith(".zip")]

def one(pred):
    for a in zips:
        if pred(a["name"]):
            return a["browser_download_url"]
    return None

u64 = one(lambda n: "arm64-v8a" in n)
u7 = one(lambda n: "armeabi-v7a" in n)
u86 = one(lambda n: "x86_64" in n)

if not all((u64, u7, u86)):
    names = [a["name"] for a in zips]
    print(
        "error: need arm64-v8a, armeabi-v7a, and x86_64 zips. Found:",
        *names,
        sep="\n  ",
        file=sys.stderr,
    )
    raise SystemExit(1)

for abi, url in (
    ("arm64-v8a", u64),
    ("armeabi-v7a", u7),
    ("x86_64", u86),
):
    print(f"{abi}\t{url}")
