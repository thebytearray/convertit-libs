#!/usr/bin/env bash
# Fetches the *latest* release's Android ImageMagick 7 *shared* build zips from
# https://github.com/codewithtamim/Android-ImageMagick7/releases (via GitHub API) and
# copies into core/native/image-magick/src/main/jniLibs/<abi>/ the same layout as this project
# has always used:
#   libmagick_bin.so  (renamed from upstream's shared/magick so AGP packages it in jni/)
#   *.so from shared/ (libc++_shared, libomp, libmagick*, …)
#
# Run from repo root: ./scripts/fetch-imagemagick-jnilibs.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JNILIBS="${ROOT}/core/native/image-magick/src/main/jniLibs"
API="https://api.github.com/repos/codewithtamim/Android-ImageMagick7/releases/latest"

command -v python3 >/dev/null 2>&1 || {
  echo "error: python3 is required to read the latest release from the GitHub API" >&2
  exit 1
}

TMP="$(mktemp -d)"
trap 'rm -rf "${TMP}"' EXIT

N=0
while IFS=$'\t' read -r abi url; do
  [[ -z "${abi}" ]] && continue
  N=$((N + 1))
  name="${url##*/}"
  echo "==> ${abi} <- ${name}"
  curl -fL --retry 3 --retry-delay 2 -o "${TMP}/${name}" "${url}"
  unzip -q -o "${TMP}/${name}" -d "${TMP}/ex_${abi}"
  if [[ ! -d "${TMP}/ex_${abi}/shared" ]]; then
    echo "error: no shared/ in ${name}; release layout may have changed" >&2
    exit 1
  fi
  dest="${JNILIBS}/${abi}"
  rm -rf "${dest}"
  mkdir -p "${dest}"
  shopt -s nullglob
  for so in "${TMP}/ex_${abi}/shared/"*.so; do
    [[ -e "$so" ]] || { echo "error: no .so in shared/ for ${abi}" >&2; exit 1; }
    cp -a "$so" "${dest}/"
  done
  if [[ ! -f "${TMP}/ex_${abi}/shared/magick" ]]; then
    echo "error: missing shared/magick for ${abi}" >&2
    exit 1
  fi
  # Same entry point name this module has always used (packaged in jni/ as a .so for AGP).
  cp -a "${TMP}/ex_${abi}/shared/magick" "${dest}/libmagick_bin.so"
  rm -rf "${TMP}/ex_${abi}"
  echo "     -> ${dest} ($(ls -1 "${dest}" | wc -l | tr -d ' ') files)"
done < <(IMAGEMAGICK_RELEASE_API_URL="${API}" python3 "${ROOT}/scripts/fetch_imagemagick_jnilibs.py")

if [[ "${N}" -ne 3 ]]; then
  echo "error: expected 3 ABI download rows from GitHub (got ${N})" >&2
  exit 1
fi

TAG_LINE="$(IMAGEMAGICK_RELEASE_API_URL="${API}" python3 -c "import os,json,urllib.request; r=urllib.request.urlopen(urllib.request.Request(os.environ['IMAGEMAGICK_RELEASE_API_URL'], headers={'Accept':'application/vnd.github+json','X-GitHub-Api-Version':'2022-11-28'})); print(json.load(r)['tag_name'])")" || true
echo "Done: release ${TAG_LINE} -> ${JNILIBS}/{arm64-v8a,armeabi-v7a,x86_64} (libmagick_bin.so + shared .so from latest only)."
