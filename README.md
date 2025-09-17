# Cloudflare Steam Name Update Service

![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)
[![GitHub Sponsors](https://img.shields.io/badge/Sponsor-gray?&logo=GitHub-Sponsors&logoColor=EA4AAA)](https://github.com/sponsors/StefanOltmann)

A **Cloudflare Worker** to update user-chosen names for salted Steam IDs in a `names.json` file stored on **Cloudflare R2**.

Proudly made with Kotlin/JS.

This service is part of my [ONI Seed Browser](https://stefan-oltmann.de/oni-seed-browser).

## Why this setup?

At first glance, maintaining a JSON file in R2 may seem unusual. However, this design offers several advantages:

- **High read frequency** – the `names.json` file is requested often.
- **Free R2 egress** – cost-efficient file serving.
- **Simple persistence model** – easy to update and distribute.

Aside from that, I like to fiddle around with Kotlin & Cloudflare Workers. :)

## How it works

1. A user provides a **salted Steam ID** and a **chosen display name**.
2. The Worker updates the `names.json` file on R2 with this mapping.
3. Other parts of the ONI Seed Browser read `names.json` directly from R2.

## Project Structure

- **Cloudflare Worker** – handles incoming requests and performs updates.
- **R2 bucket** – stores `names.json`.
- **names.json** – JSON map of `saltedSteamId → name`.

Example `names.json`:

```json
{
    "abcd1234salted": "PlayerOne",
    "efgh5678salted": "CoolName42"
}
```

## Run & Deploy

```
wrangler dev
```

```
wrangler deploy
```

## License

This Cloudflare worker is licensed under the GNU Affero General Public License (AGPL),
ensuring the community's freedom to use, modify, and distribute the software.

In short, you can’t make a closed-source copy of the entire project,
but you're welcome to study it and reuse parts in your own projects.
