# Architecture

## Device snapshots
- Stored in `users/{userId}/devices/{deviceId}` (one doc per device).
- Device doc fields: `deviceId`, `platform`, `osVersion`, `apiLevel`, `manufacturer`, `model`, `appVersion`, `firstSeenAt`, `lastSeenAt`.
- User doc only keeps `lastDeviceId` (no list of devices).
- Global queries should use `collectionGroup("devices")` with the needed indexes.
