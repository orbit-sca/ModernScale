# Kember.co - Full Stack Scala Application

A full-stack web application built with ZIO, Laminar, ScalaJS, and Tapir, implementing the Base44 design system.

## Tech Stack

### Backend
- **ZIO 2.0** - Effect system and concurrency
- **Tapir** - Type-safe endpoint definitions
- **ZIO HTTP** - HTTP server
- **Quill** - Database access with ZIO integration
- **PostgreSQL** - Database
- **Flyway** - Database migrations

### Frontend
- **ScalaJS** - Scala to JavaScript compiler
- **Laminar** - Reactive UI library
- **Waypoint** - Routing
- **Tapir Client** - Type-safe API client
- **Vite** - Build tool and dev server

### Shared
- **Tapir** - Shared endpoint definitions
- **ZIO JSON** - JSON serialization
- **Domain models** - Shared between frontend and backend

## Project Structure

```
kemberco/
├── build.sbt                    # SBT build configuration
├── project/
│   ├── build.properties         # SBT version
│   └── plugins.sbt              # SBT plugins (ScalaJS, etc.)
│
├── modules/
│   ├── shared/                  # Cross-compiled (JVM + JS)
│   │   └── src/main/scala/com/kember/
│   │       ├── domain/          # Domain models
│   │       └── api/             # Tapir endpoint definitions
│   │
│   ├── backend/                 # JVM only
│   │   └── src/main/scala/com/kember/
│   │       ├── Application.scala
│   │       ├── config/          # App configuration
│   │       ├── repositories/    # Database access (Quill)
│   │       ├── services/        # Business logic
│   │       └── http/            # ZIO HTTP + Tapir routes
│   │
│   └── frontend/                # ScalaJS only
│       ├── package.json         # npm configuration
│       ├── vite.config.js       # Vite configuration
│       ├── index.html           # HTML entry point
│       ├── styles.css           # Global CSS
│       └── src/main/scala/com/kember/
│           ├── Main.scala       # ScalaJS entry point
│           ├── theme/
│           │   └── DesignTokens.scala
│           ├── components/
│           │   ├── common/      # Buttons, inputs, cards
│           │   │   └── KemberComponents.scala
│           │   ├── layout/      # Navigation, footer
│           │   └── pages/       # Homepage, Contact, About
│           └── routing/         # Laminar routing
```

## Getting Started

### Prerequisites
- Java 11 or higher
- SBT 1.11.7
- Node.js 18+ and npm (for frontend tooling)

### Installation

1. **Install npm dependencies for frontend:**
   ```bash
   cd modules/frontend
   npm install
   cd ../..
   ```

2. **Compile the project:**
   ```bash
   sbt compile
   ```

3. **Compile ScalaJS (frontend):**
   ```bash
   sbt frontend/fastLinkJS
   ```

### Development Workflow

#### Backend Development

Run the backend server:
```bash
sbt backend/run
```

The backend will start on `http://localhost:8080`

#### Frontend Development

1. **Compile ScalaJS in watch mode:**
   ```bash
   sbt ~frontend/fastLinkJS
   ```

2. **In another terminal, run Vite dev server:**
   ```bash
   cd modules/frontend
   npm run dev
   ```

The frontend will be available at `http://localhost:3000`

Vite will proxy API requests to the backend at `http://localhost:8080`

#### Full Stack Development

Use two terminals:
- Terminal 1: `sbt backend/run`
- Terminal 2: `sbt ~frontend/fastLinkJS`
- Terminal 3: `cd modules/frontend && npm run dev`

### Building for Production

1. **Compile backend:**
   ```bash
   sbt backend/assembly
   ```

2. **Build optimized frontend:**
   ```bash
   sbt frontend/fullLinkJS
   cd modules/frontend
   npm run build
   ```

## Design System

The project uses the Base44 design system, implemented in Scala.

### Design Tokens

All design tokens are centralized in `modules/frontend/src/main/scala/com/kember/theme/DesignTokens.scala`

Key features:
- **Colors**: Primary (#2D5A47), Secondary (#7A9E7E), Accent (#F5F1EB)
- **Typography**: Font families, sizes, weights, line heights
- **Spacing**: Consistent spacing scale (xs to giant)
- **Shadows**: Subtle to heavy shadows
- **Layout**: Container widths, padding, grid systems
- **Breakpoints**: Mobile, tablet, desktop

### Using Components

Example usage of the pre-built components:

```scala
import com.kember.components.common.KemberComponents.*
import com.kember.theme.DesignTokens.*

// Button
primaryButton("Click Me", () => println("Clicked!"))

// Badge
badge("New Feature", Some("🎉"))

// Feature Card
featureCard(
  icon = "🏠",
  title = "Guaranteed Rent",
  description = "We pay you every month, no matter what"
)

// Grid Layout
grid(4)(
  featureCard(...),
  featureCard(...),
  featureCard(...),
  featureCard(...)
)
```

## Project Modules

### Shared Module

Define domain models and API endpoints that are shared between backend and frontend:

```scala
// modules/shared/src/main/scala/com/kember/domain/User.scala
package com.kember.domain

import zio.json.*

case class User(
  id: Long,
  name: String,
  email: String
) derives JsonCodec

// modules/shared/src/main/scala/com/kember/api/UserEndpoints.scala
package com.kember.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import com.kember.domain.User

object UserEndpoints:
  val getUser: PublicEndpoint[Long, String, User, Any] =
    endpoint
      .get
      .in("api" / "users" / path[Long]("id"))
      .out(jsonBody[User])
      .errorOut(stringBody)
```

### Backend Module

Implement the endpoints defined in the shared module:

```scala
// modules/backend/src/main/scala/com/kember/http/UserRoutes.scala
package com.kember.http

import zio.*
import zio.http.*
import sttp.tapir.server.ziohttp.*
import com.kember.api.UserEndpoints

object UserRoutes:
  val getUserRoute =
    ZioHttpInterpreter().toHttp(UserEndpoints.getUser) { id =>
      // Implementation
      ZIO.succeed(User(id, "John Doe", "john@example.com"))
    }
```

### Frontend Module

Call the backend API from the frontend:

```scala
// modules/frontend/src/main/scala/com/kember/components/pages/UserPage.scala
package com.kember.components.pages

import com.raquo.laminar.api.L.{*, given}
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.client3.*
import com.kember.api.UserEndpoints

object UserPage:
  def apply(userId: Long): HtmlElement =
    div(
      h1("User Profile"),
      // Use Tapir client to call the API
      // Implementation here
    )
```

## Next Steps

1. **Set up database**: Configure PostgreSQL and create initial migration
2. **Define domain models**: Add your business entities to `modules/shared/src/main/scala/com/kember/domain/`
3. **Create API endpoints**: Define Tapir endpoints in `modules/shared/src/main/scala/com/kember/api/`
4. **Build pages**: Implement homepage, contact page, etc. using the design system components
5. **Add routing**: Set up Waypoint routing in the frontend
6. **Connect to backend**: Use Tapir client in frontend to call backend APIs

## Useful Commands

```bash
# Compile everything
sbt compile

# Run tests
sbt test

# Compile ScalaJS (development)
sbt frontend/fastLinkJS

# Compile ScalaJS (optimized)
sbt frontend/fullLinkJS

# Continuous compilation
sbt ~frontend/fastLinkJS

# Run backend
sbt backend/run

# Format code
sbt scalafmt

# Check for updates
sbt dependencyUpdates
```

## Resources

- [ZIO Documentation](https://zio.dev)
- [Laminar Documentation](https://laminar.dev)
- [Tapir Documentation](https://tapir.softwaremill.com)
- [ScalaJS Documentation](https://www.scala-js.org)
- [Quill Documentation](https://getquill.io)
