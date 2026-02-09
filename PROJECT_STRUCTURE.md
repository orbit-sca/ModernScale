# Kember.co - Project Structure Overview

## Architecture Summary

This is a **full-stack Scala application** with:
- **Shared code** between frontend and backend (domain models + API definitions)
- **Type-safe APIs** using Tapir (same endpoint definition for server and client)
- **Functional programming** with ZIO effect system
- **Reactive UI** with Laminar (no virtual DOM, direct DOM manipulation)
- **Modern tooling** with Vite for fast development

## Module Breakdown

### 1. Shared Module (`modules/shared/`)
**Purpose:** Code compiled for both JVM (backend) and JavaScript (frontend)

**Contents:**
- `domain/` - Domain models (User, Product, ContactForm, etc.)
- `api/` - Tapir endpoint definitions

**Key Benefit:** Define your API contract once, use everywhere

**Example:**
```scala
// Define once
val getUser: Endpoint[Long, String, User, Any] = ...

// Backend: Implement the endpoint
val route = getUser.serverLogic { id => ... }

// Frontend: Call the endpoint
val request = getUser.toRequest(userId)
```

### 2. Backend Module (`modules/backend/`)
**Purpose:** JVM server application

**Tech Stack:**
- ZIO 2.0 for effects and concurrency
- ZIO HTTP for the HTTP server
- Tapir for type-safe routing
- Quill for database access
- PostgreSQL for persistence
- Flyway for database migrations

**Structure:**
```
backend/
├── Application.scala          # Main entry point
├── config/                    # Configuration (via ZIO Config)
├── repositories/              # Database layer (Quill)
│   └── UserRepository.scala
├── services/                  # Business logic
│   └── UserService.scala
└── http/                      # HTTP routes
    └── UserRoutes.scala
```

**Flow:**
```
HTTP Request
  → Routes (Tapir endpoints)
  → Services (business logic)
  → Repositories (database)
  → Response
```

### 3. Frontend Module (`modules/frontend/`)
**Purpose:** ScalaJS browser application

**Tech Stack:**
- ScalaJS (Scala → JavaScript compilation)
- Laminar (reactive UI library)
- Waypoint (routing)
- Tapir Client (type-safe API calls)
- Vite (dev server + bundling)

**Structure:**
```
frontend/
├── Main.scala                 # ScalaJS entry point
├── theme/
│   └── DesignTokens.scala    # Design system constants
├── components/
│   ├── common/                # Reusable components
│   │   └── KemberComponents.scala
│   ├── layout/                # Layout components
│   │   ├── Navigation.scala
│   │   └── Footer.scala
│   └── pages/                 # Page components
│       ├── HomePage.scala
│       ├── ContactPage.scala
│       └── AboutPage.scala
└── routing/
    └── Router.scala           # Waypoint routing setup
```

**Component Hierarchy:**
```
Main.scala
  └── Router
      ├── HomePage
      │   ├── Navigation
      │   ├── HeroSection
      │   ├── FeaturesSection
      │   └── Footer
      ├── ContactPage
      │   ├── Navigation
      │   ├── ContactForm
      │   └── Footer
      └── AboutPage
          ├── Navigation
          ├── AboutContent
          └── Footer
```

## Data Flow Example

### Submitting a Contact Form

**1. User fills out form (Frontend)**
```scala
// ContactPage.scala
val nameVar = Var("")
val emailVar = Var("")

div(
  textInput("Name").amend(
    onInput.mapToValue --> nameVar
  ),
  textInput("Email").amend(
    onInput.mapToValue --> emailVar
  ),
  primaryButton("Submit", () => {
    submitForm(ContactForm(nameVar.now(), emailVar.now()))
  })
)
```

**2. API call using Tapir client (Frontend)**
```scala
def submitForm(form: ContactForm): Unit =
  val request = ContactEndpoints.submit.toRequest(form)
  // Execute with sttp client
  // Handle response
```

**3. Endpoint definition (Shared)**
```scala
val submit: Endpoint[ContactForm, String, String, Any] =
  endpoint.post
    .in("api" / "contact")
    .in(jsonBody[ContactForm])
    .out(stringBody)
```

**4. Route handler (Backend)**
```scala
val submitRoute =
  ZioHttpInterpreter().toHttp(ContactEndpoints.submit) { form =>
    for
      _ <- contactService.processForm(form)
      _ <- emailService.sendNotification(form)
      response <- ZIO.succeed("Thank you!")
    yield response
  }
```

**5. Service layer (Backend)**
```scala
def processForm(form: ContactForm): Task[Unit] =
  for
    _ <- contactRepo.save(form)
    _ <- ZIO.logInfo(s"Saved contact from ${form.email}")
  yield ()
```

## Design System Integration

### Tokens → Components → Pages

**Level 1: Design Tokens**
```scala
object Colors:
  val primary = "#2D5A47"
  val accent = "#F5F1EB"
```

**Level 2: Components**
```scala
def primaryButton(text: String) =
  button(
    text,
    styleAttr := s"background: ${Colors.primary}; ..."
  )
```

**Level 3: Pages**
```scala
def HomePage =
  div(
    section(
      h1("Welcome"),
      primaryButton("Get Started")
    )
  )
```

## Build Process

### Development Flow

**Backend:**
```
.scala files → SBT → .class files → JVM
```

**Frontend:**
```
.scala files → SBT + ScalaJS → .js files → Browser
                                    ↓
                                  Vite (dev server)
```

### Production Build

**Backend:**
```bash
sbt backend/assembly
# → target/scala-3.7.4/kember-backend-assembly-0.1.0-SNAPSHOT.jar
```

**Frontend:**
```bash
sbt frontend/fullLinkJS  # Optimized ScalaJS compilation
cd modules/frontend
npm run build            # Vite production build
# → modules/frontend/dist/ (static assets)
```

## File Organization Best Practices

### Domain Models
- Place in `modules/shared/src/main/scala/com/kember/domain/`
- Use `derives JsonCodec` for automatic JSON serialization
- Keep them pure (no business logic)

### API Endpoints
- Define in `modules/shared/src/main/scala/com/kember/api/`
- Group by domain (UserEndpoints, ContactEndpoints, etc.)
- One file per domain entity

### Backend Services
- Business logic in `modules/backend/src/main/scala/com/kember/services/`
- Database access in `modules/backend/src/main/scala/com/kember/repositories/`
- HTTP routes in `modules/backend/src/main/scala/com/kember/http/`

### Frontend Components
- Reusable components in `components/common/`
- Layout components in `components/layout/`
- Page-specific components in `components/pages/`

## Development Workflow

### Typical Development Session

1. **Start backend server:**
   ```bash
   sbt backend/run
   ```

2. **Start ScalaJS compiler in watch mode:**
   ```bash
   sbt ~frontend/fastLinkJS
   ```

3. **Start Vite dev server:**
   ```bash
   cd modules/frontend && npm run dev
   ```

4. **Make changes:**
   - Edit Scala files
   - ScalaJS auto-recompiles (watch mode)
   - Vite auto-reloads browser
   - See changes instantly

### Adding a New Feature

1. **Define domain model** in `shared/domain/`
2. **Define API endpoint** in `shared/api/`
3. **Implement backend**:
   - Repository (database)
   - Service (business logic)
   - Route (HTTP handler)
4. **Implement frontend**:
   - Create page component
   - Add to routing
   - Make API calls using Tapir client

## Key Files Reference

| File | Purpose |
|------|---------|
| `build.sbt` | SBT build configuration, dependencies, modules |
| `modules/frontend/package.json` | npm dependencies (Vite, etc.) |
| `modules/frontend/vite.config.js` | Vite configuration, proxy setup |
| `modules/frontend/index.html` | HTML entry point |
| `modules/frontend/styles.css` | Global CSS, design tokens |
| `modules/backend/src/main/scala/com/kember/Application.scala` | Backend entry point |
| `modules/frontend/src/main/scala/com/kember/Main.scala` | Frontend entry point |

## Summary

This architecture provides:
- ✅ Type safety end-to-end (from database to UI)
- ✅ Code sharing between frontend and backend
- ✅ Fast development with hot reload
- ✅ Functional programming with ZIO
- ✅ Modern reactive UI with Laminar
- ✅ Consistent design system
- ✅ Production-ready build process

The key insight: **One API definition, type-safe everywhere.**
