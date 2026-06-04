# HAProxy Syntax Highlighting for JetBrains IDEs

Syntax highlighting for HAProxy configuration files in any JetBrains IDE — PhpStorm, WebStorm, IntelliJ IDEA, GoLand, Rider, and more.

No other plugin currently provides this.

---

## Features

- **Section keywords** — `global`, `defaults`, `frontend`, `backend`, `listen`, `resolvers`, `peers`, `userlist`, `cache`, `fcgi-app`, and more
- **Section names** — the user-defined name after each section keyword (`backend cluster_app`, `frontend https`, etc.)
- **Directive keywords** — every line-start directive across all sections (`bind`, `server`, `option`, `timeout`, `acl`, `http-request`, `balance`, `mode`, `log`, `redirect`, `capture`, `filter`, `stick-table`, `cookie`, `stats`, …)
- **Sub-command keywords** — the second-level keyword on a line (`set-header` after `http-request`, `roundrobin` after `balance`, `connect` after `timeout`, `socket` / `level` / `expose-fd` after `stats`, `insert` / `dynamic` after `cookie`, …)
- **Option values** — `forwardfor`, `httplog`, `dontlognull`, `http-server-close`, `ssl-hello-chk`, and the full `option` keyword list
- **ACL fetch methods** — `hdr_end`, `path_beg`, `src`, `ssl_fc`, `req.hdr`, `dst_port`, `sc0_http_req_rate`, `urlp`, and the complete set of built-in fetch methods and converters
- **ACL comparison operators** — `eq`, `ne`, `lt`, `le`, `gt`, `ge`
- **SSL option values** — `prefer-client-ciphers`, `no-tls-tickets`, `no-tlsv10`, `no-tlsv11`, `no-tlsv12`, `no-tlsv13`, and the full `ssl-default-*-options` value list
- **Numbers and time values** — `5s`, `120m`, `28000`, etc.
- **Quoted strings**
- **Comments**
- **User-defined name references** — backend names, ACL names, and server names are highlighted wherever they're referenced (e.g. `cluster_app` in `use_backend cluster_app if host_app` matches the `backend cluster_app` definition)

All colours are fully customisable via **Settings → Editor → Color Scheme → HAProxy**.

---

## Installation

### From the JetBrains Plugin Marketplace
> Coming soon.

### From a built ZIP
1. Clone the repo and build:
   ```bash
   git clone git@github.com:Celtech/haproxy-jetbrains-syntax.git
   cd haproxy-jetbrains-syntax
   ./gradlew buildPlugin
   ```
2. In your IDE go to **Settings → Plugins → ⚙ → Install Plugin from Disk**
3. Select `build/distributions/haproxy-highlight-1.0.0.zip`
4. Restart the IDE

---

## File detection

The plugin activates on:

| Pattern | Example |
|---|---|
| `.haproxy` extension | `mysite.haproxy` |
| Named `haproxy.cfg` | `haproxy.cfg` |
| Named `haproxy.conf` | `haproxy.conf` |
| Glob `*.haproxy.cfg` | `prod.haproxy.cfg` |
| Glob `*.haproxy.conf` | `prod.haproxy.conf` |

---

## Building from source

**Requirements:** Java 21, Gradle 8.8 (wrapper included)

```bash
# run the plugin in a sandbox IDE (downloads IntelliJ CE 2024.3 on first run)
./gradlew runIde

# build the distributable ZIP
./gradlew buildPlugin
```

If your system Java is newer than 21, point Gradle at a Java 21 installation:

```bash
JAVA_HOME=/path/to/java21 ./gradlew buildPlugin
```

---

## Customising colours

Open **Settings → Editor → Color Scheme → HAProxy** to adjust any of the token colours:

| Token | Default |
|---|---|
| Comment | Line comment (grey) |
| Section keyword | Keyword (orange) |
| Section name | Class name (yellow) |
| Directive keyword | Keyword (orange) |
| Sub-command keyword | Function declaration (light orange) |
| Option value | Function declaration (light orange) |
| Parameter keyword | Function declaration (light orange) |
| Number | Number (blue) |
| String | String (green) |
| User-defined name reference | Instance field (purple) |

---

## Roadmap

- [ ] Code completion for directives, option values, and ACL fetch methods
- [ ] Go-to-definition / find-usages for user-defined names (backends, ACLs, servers)
- [ ] Inline error detection for invalid directive combinations
- [ ] JetBrains Plugin Marketplace release

The plugin is built on a minimal flat `ParserDefinition` specifically so that swapping in a full Grammar-Kit parser for completion support is a drop-in change with no impact on the existing highlighting.

---

## License

MIT
