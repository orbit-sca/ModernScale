# Kember.co - Quick Start Guide

## 5-Minute Setup

### 1. Install Frontend Dependencies
```bash
cd modules/frontend
npm install
cd ../..
```

### 2. Start Development

Open three terminals:

**Terminal 1 - Backend:**
```bash
sbt backend/run
```

**Terminal 2 - ScalaJS Compiler (watch mode):**
```bash
sbt ~frontend/fastLinkJS
```

**Terminal 3 - Vite Dev Server:**
```bash
cd modules/frontend
npm run dev
```

Visit `http://localhost:3000` in your browser!

## Project Structure at a Glance

```
modules/
├── shared/          # Code shared between frontend & backend
│   ├── domain/      # Your data models (User, Product, etc.)
│   └── api/         # Tapir endpoint definitions
│
├── backend/         # JVM server code
│   ├── Application.scala
│   ├── config/      # Configuration
│   ├── repositories/# Database (Quill + PostgreSQL)
│   ├── services/    # Business logic
│   └── http/        # HTTP routes (ZIO HTTP + Tapir)
│
└── frontend/        # ScalaJS browser code
    ├── Main.scala
    ├── theme/       # DesignTokens.scala
    ├── components/  # Reusable UI components
    │   ├── common/  # Buttons, cards, forms
    │   ├── layout/  # Nav, footer, sections
    │   └── pages/   # Full pages
    └── routing/     # Waypoint routing
```

## Building Your First Feature

### Step 1: Define a Domain Model (Shared)

`modules/shared/src/main/scala/com/kember/domain/ContactForm.scala`
```scala
package com.kember.domain

import zio.json.*

case class ContactForm(
  name: String,
  email: String,
  message: String
) derives JsonCodec
```

### Step 2: Define API Endpoint (Shared)

`modules/shared/src/main/scala/com/kember/api/ContactEndpoints.scala`
```scala
package com.kember.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import com.kember.domain.ContactForm

object ContactEndpoints:
  val submit: PublicEndpoint[ContactForm, String, String, Any] =
    endpoint
      .post
      .in("api" / "contact")
      .in(jsonBody[ContactForm])
      .out(stringBody)
      .errorOut(stringBody)
```

### Step 3: Implement Backend (Backend)

`modules/backend/src/main/scala/com/kember/http/ContactRoutes.scala`
```scala
package com.kember.http

import zio.*
import zio.http.*
import sttp.tapir.server.ziohttp.*
import com.kember.api.ContactEndpoints
import com.kember.domain.ContactForm

object ContactRoutes:
  val submitRoute =
    ZioHttpInterpreter().toHttp(ContactEndpoints.submit) { form =>
      // Process the contact form
      ZIO.logInfo(s"Received contact from: ${form.name}") *>
      ZIO.succeed("Thank you for contacting us!")
    }
```

### Step 4: Call from Frontend (Frontend)

`modules/frontend/src/main/scala/com/kember/components/pages/ContactPage.scala`
```scala
package com.kember.components.pages

import com.raquo.laminar.api.L.{*, given}
import com.kember.components.common.KemberComponents.*
import com.kember.domain.ContactForm
// Add Tapir client setup

object ContactPage:
  def apply(): HtmlElement =
    div(
      h1("Contact Us"),
      // Form with textInput, textArea, primaryButton
      // Use Tapir client to submit form
    )
```

## Using the Design System

### Available Components

Import components:
```scala
import com.kember.components.common.KemberComponents.*
import com.kember.theme.DesignTokens.*
```

**Buttons:**
```scala
primaryButton("Click Me", () => println("Clicked!"))
secondaryButton("Learn More", () => ())
```

**Badge:**
```scala
badge("New Feature")
```

**Cards:**
```scala
featureCard(
  icon = "🏠",
  title = "Guaranteed Rent",
  description = "Get paid every month"
)

processCard(
  number = "01",
  icon = "💬",
  title = "Let's Chat",
  description = "We discuss your needs"
)
```

**Forms:**
```scala
textInput("Your Name", "text")
textArea("Your Message", rows = 4)
```

**Layout:**
```scala
section(
  h2("Features"),
  grid(4)(
    featureCard(...),
    featureCard(...),
    featureCard(...),
    featureCard(...)
  )
)
```

### Design Tokens

Use design tokens for custom styling:
```scala
div(
  styleAttr := s"""
    color: ${Colors.primary};
    padding: ${Spacing.lg};
    border-radius: ${BorderRadius.md};
    box-shadow: ${Shadow.subtle};
  """
)
```

## Common Tasks

### Add a New Page

1. Create `modules/frontend/src/main/scala/com/kember/components/pages/AboutPage.scala`
2. Build the page using components
3. Add route in routing configuration
4. Link to it from navigation

### Add a New API Endpoint

1. Define domain model in `modules/shared/src/main/scala/com/kember/domain/`
2. Define endpoint in `modules/shared/src/main/scala/com/kember/api/`
3. Implement handler in `modules/backend/src/main/scala/com/kember/http/`
4. Call from frontend using Tapir client

### Add Database Access

1. Create repository in `modules/backend/src/main/scala/com/kember/repositories/`
2. Use Quill for type-safe queries
3. Inject repository into your service layer
4. Call from HTTP routes

## Helpful SBT Commands

```bash
# Continuous compilation (watches for changes)
sbt ~frontend/fastLinkJS

# Run tests
sbt test

# Check what projects are available
sbt projects

# Switch to a project
sbt
> project frontend
> compile

# Format code
sbt scalafmt

# Clean build
sbt clean
```

## Troubleshooting

**ScalaJS compilation errors?**
- Make sure you're in the right module: `sbt frontend/fastLinkJS`
- Check that imports use `com.kember` package

**Vite can't find main.js?**
- Run `sbt frontend/fastLinkJS` first
- Check that output is in `modules/frontend/dist/`

**Backend won't start?**
- Check that port 8080 is available
- Look for compilation errors in backend module

**Frontend API calls failing?**
- Ensure backend is running on port 8080
- Check Vite proxy configuration in `vite.config.js`

## Next Steps

1. ✅ Run the app locally
2. 📝 Build the homepage using design system components
3. 📋 Create contact form page
4. 🗄️ Set up PostgreSQL database
5. 🔐 Add user authentication
6. 🚀 Deploy to production

Happy coding! 🎉
