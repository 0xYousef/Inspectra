# CPU & Network Throttling — Cheat Sheet

This feature throttles Chrome/Chromium sessions via the Chrome DevTools Protocol
(CDP). It is implemented in `core.throttle` and auto-applied by `DriverFactory`
when enabled.

## Configuration

Configuration follows a strict 4-layer chain (first match wins):

1. System property — `-Dthrottle.enabled=true`
2. Environment variable — `THROTTLE_ENABLED=true`
3. `.env` file — `throttle.enabled=true`
4. Built-in default

`application.properties` is **not** consulted.

## Environment keys & values

| Env var / property | Values | Default | Notes |
|---|---|---|---|
| `THROTTLE_ENABLED` / `throttle.enabled` | `true`, `false` | `false` | Master switch; when `false` no throttling is applied |
| `THROTTLE_CPU_PROFILE` / `throttle.cpu.profile` | `NORMAL`, `X2`, `X4`, `X6`, `X8`, `X10`, `X20` | `NORMAL` | Slow-down multiplier passed to `Emulation.setCPUThrottlingRate` |
| `THROTTLE_NETWORK_PROFILE` / `throttle.network.profile` | `WIFI`, `FAST_4G`, `FOUR_G`, `SLOW_4G`, `FAST_3G`, `THREE_G`, `SLOW_3G`, `TWO_G`, `EDGE`, `GPRS`, `OFFLINE` | *(unset — network not throttled)* | Leave unset to keep the network untouched |

Values are case-insensitive and accept `-` in place of `_` (e.g. `fast-4g`).
Invalid values throw `data.exceptions.ConfigurationException`.

## Network profile presets

| Profile   | Download    | Upload     | Latency |
|-----------|-------------|------------|---------|
| `WIFI`    | 30 Mbps     | 15 Mbps    | 5 ms    |
| `FAST_4G` | 30 Mbps     | 15 Mbps    | 20 ms   |
| `FOUR_G`  | 20 Mbps     | 10 Mbps    | 40 ms   |
| `SLOW_4G` | 400 kbps    | 400 kbps   | 150 ms  |
| `FAST_3G` | 1.6 Mbps    | 750 kbps   | 75 ms   |
| `THREE_G` | 1.6 Mbps    | 750 kbps   | 100 ms  |
| `SLOW_3G` | 400 kbps    | 400 kbps   | 200 ms  |
| `TWO_G`   | 250 kbps    | 50 kbps    | 300 ms  |
| `EDGE`    | 240 kbps    | 200 kbps   | 300 ms  |
| `GPRS`    | 50 kbps     | 20 kbps    | 500 ms  |
| `OFFLINE` | disconnected              |           |         |

## Examples

### `.env` file (project root)

```dotenv
throttle.enabled=true
throttle.cpu.profile=X4
throttle.network.profile=THREE_G
```

### Environment variables

```bash
export THROTTLE_ENABLED=true
export THROTTLE_CPU_PROFILE=X2
export THROTTLE_NETWORK_PROFILE=FAST_3G
```

### System properties (Maven)

```bash
mvn test -Dthrottle.enabled=true -Dthrottle.cpu.profile=X4 -Dthrottle.network.profile=SLOW_3G
```

## Programmatic API (`core.throttle`)

| Type | Purpose |
|---|---|
| `CpuProfile` | Enum presets `NORMAL`…`X20` + `custom(int multiplier)` → `CpuThrottle` |
| `NetworkProfile` | Enum presets (table above) + `custom(downloadBps, uploadBps, latencyMs)` → `NetworkThrottle` |
| `CpuThrottle` / `NetworkThrottle` | Common type for presets and custom rates (used by `ThrottleManager`) |
| `NetworkSettings` | Record `(offline, latency, downloadThroughput, uploadThroughput)` — bytes/sec |
| `ThrottleOptions` | Immutable composite options + fluent `Builder` |
| `ThrottleManager` | Per-driver CDP client: `apply(...)`, `setCpu`, `setNetwork`, `reset*` |
| `ConfigManager` | Reads the 4-layer config chain |

```java
// Auto (uses configured profiles)
DriverFactory.create();            // applies config if throttle.enabled=true

// Manual override per session
ThrottleManager throttle = new ThrottleManager(driver);
throttle.apply(CpuProfile.X4, NetworkProfile.THREE_G);
driver.get("https://example.com");
throttle.reset();

// Fluent options
ThrottleOptions options = new ThrottleOptions.Builder()
        .cpu(CpuProfile.X2)
        .network(NetworkProfile.FAST_3G)
        .build();
new ThrottleManager(driver).apply(options);

// Custom profiles (raw values)
throttle.apply(CpuProfile.custom(3), NetworkProfile.custom(200_000, 100_000, 150));
// CPU 3x slower; network 200 KB/s down / 100 KB/s up, 150 ms latency
```

## Notes

- Only Chrome/Chromium-based drivers (implementing `HasDevTools`) support CDP;
  other drivers are logged and skipped during auto-apply.
- CDP command classes are the bundled `selenium-devtools-v150` (Selenium 4.46.0
  also ships v148/v149/latest); the runtime picks the nearest supported CDP
  version for the running browser.
- Demo tests live in `selenium.tests.throttle.ThrottleDemoTest` and are excluded
  from the default TestNG suites.
- `selenium.tests.contactus.TC07_ContactUs_Throttled_Test` runs the contact us
  flow under custom throttling (`CpuProfile.custom(3)` + `NetworkProfile.custom(200_000, 100_000, 150)`)
  and resets it afterwards; it is picked up by the UI suite package scan.
