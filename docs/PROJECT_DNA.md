
# Project DNA — Provenance Manifest

## Purpose

Project DNA is the per-archive provenance record. It makes a downloaded archive traceable to its source and transfer event. It does not claim that the source is safe or that the digest is signed by the provider.

## Schema version 1

| Field | Meaning |
|---|---|
| schema_version | Manifest schema version |
| project_harvester_version | App version that created the record |
| provider | GitHub or GitLab |
| source_url | Canonical public project URL |
| project | Provider's full project name/path |
| default_branch | Branch selected by provider metadata |
| description | Provider description, if supplied |
| visibility | Public/private metadata |
| license | Provider-reported license identifier/name, or Not detected |
| archived | Provider archived state |
| archive_url | Archive endpoint used for transfer |
| file_name | Destination filename |
| download_id | Android Download Manager ID |
| status | queued, downloaded_hashed, downloaded_hash_unavailable or failed |
| created_at_epoch_ms | Request time |
| downloaded_bytes | Bytes reported by Android |
| completed_at_epoch_ms | Terminal state time |
| sha256 | Locally calculated digest when readable |
| local_uri | Android Download Manager local URI when available |
| failure_reason | Numeric/system failure context when available |

## Integrity semantics

SHA-256 is calculated by streaming the completed file in 64 KiB chunks. The digest is a local fingerprint for later comparison. No trusted provider checksum is currently fetched, so downloaded_hashed does not mean cryptographic authenticity has been verified.

## Storage

Manifests are written to the app-private directory files/project-manifests/. They are not uploaded to a server.
