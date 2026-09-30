---
name: AfyaQueue Clinical Motion
colors:
  surface: '#f9f9ff'
  surface-dim: '#cfdaf2'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f3ff'
  surface-container: '#e7eeff'
  surface-container-high: '#dee8ff'
  surface-container-highest: '#d8e3fb'
  on-surface: '#111c2d'
  on-surface-variant: '#3f4940'
  inverse-surface: '#263143'
  inverse-on-surface: '#ecf1ff'
  outline: '#6f7a70'
  outline-variant: '#bec9be'
  surface-tint: '#066d3a'
  primary: '#006032'
  on-primary: '#ffffff'
  primary-container: '#1e7a46'
  on-primary-container: '#adffc2'
  inverse-primary: '#81d99b'
  secondary: '#a33e00'
  on-secondary: '#ffffff'
  secondary-container: '#fc8044'
  on-secondary-container: '#662400'
  tertiary: '#435278'
  on-tertiary: '#ffffff'
  tertiary-container: '#5b6a92'
  on-tertiary-container: '#e8ecff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#9df6b6'
  primary-fixed-dim: '#81d99b'
  on-primary-fixed: '#00210d'
  on-primary-fixed-variant: '#00522a'
  secondary-fixed: '#ffdbcd'
  secondary-fixed-dim: '#ffb596'
  on-secondary-fixed: '#360f00'
  on-secondary-fixed-variant: '#7c2e00'
  tertiary-fixed: '#dae2ff'
  tertiary-fixed-dim: '#b7c6f3'
  on-tertiary-fixed: '#081a3e'
  on-tertiary-fixed-variant: '#37466c'
  background: '#f9f9ff'
  on-background: '#111c2d'
  surface-variant: '#d8e3fb'
typography:
  display-lg:
    fontFamily: Manrope
    fontSize: 44px
    fontWeight: '800'
    lineHeight: 52px
    letterSpacing: -0.02em
  display-sm:
    fontFamily: Manrope
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Manrope
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: 0em
  headline-sm:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: 0em
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0em
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
    letterSpacing: 0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.01em
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.01em
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.03em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-sm: 0.75rem
  margin: 1rem
  margin-sm: 0.75rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a high-trust, low-stress healthcare environment tailored for mobile clinical queues and appointments. Built upon Material Design 3 guidelines with a specialized medical cadence, it balances reassuring clinical stability with clear operational urgency.

### Core Persona & Emotional Goal
- **Reassurance & Clarity:** Patients often interact with the interface while experiencing stress, vulnerability, or time pressure. Visual elements must eliminate cognitive ambiguity, providing immediate comprehension of wait times, current positions, and appointment updates.
- **Precision & Authority:** High legibility, crisp structural lines, and dependable contrast ratios ensure medical accessibility compliance across diverse ambient lighting conditions (bright clinic lobbies to low-light waiting rooms).

### Visual Aesthetic
The style is **Modern Clinical M3 with Tonal Depth**:
- Layered, soft surfaces derived from mint-infused off-whites avoid clinical sterile coldness.
- Deep forest tones establish an organic sense of health, stability, and care.
- An earthy terracotta accent introduces an intentional alert and action hue without triggering the panic associated with standard medical emergency crimson.

## Colors

The palette uses deliberate clinical functional semantics:

- **Primary (`#1E7A46` - Forest Green):** Represents clinical trust, operational progress, active queue confirmations, and verified booking flows.
- **Secondary (`#C1541A` - Warm Terracotta Accent):** Reserved strictly for critical state changes, call-to-action buttons requiring time-sensitive attention, active alerts, and "Now Calling" queue highlights.
- **Tertiary (`#16264A` - Dark Midnight Navy):** Provides high-contrast anchor architecture for top app bars, primary ticket headers, and clinical facility branding blocks.
- **Mint Light Surface (`#F0F7F4`):** The primary canvas and background tint, reducing screen glare in clinical waiting areas.
- **Crisp White (`#FFFFFF`):** Base for raised queue cards, sheet surfaces, modal dialogs, and elevated ticket containers.
- **Subtle Cool Gray (`#F4F6F8`):** Used for non-interactive backgrounds, segment dividers, and inactive state fills.
- **Slate Dark Text (`#1E293B`):** Deep, warm slate for primary typography ensuring WCAG AAA accessibility on white and mint surfaces.

### Functional Roles & Contrast Rules
- High-priority interactive targets use Primary Forest Green or Secondary Terracotta with white text.
- Live tracking containers layer white surfaces over Mint surfaces with subtle 1px border lines tint-matched to Primary Green at 12% opacity.
- Inactive or expired queue cards drop to Cool Gray backgrounds with muted Slate text at 60% opacity.

## Typography

The pairing of **Manrope** for display/headlines and **Plus Jakarta Sans** for body and controls creates a clean, humanistic clinical aesthetic.

- **Manrope:** Delivers clean geometric stability and confident numeral shapes, essential for queue counter tickets, token digits, and clinical department headers.
- **Plus Jakarta Sans:** Provides wide apertures, clear distinction between similar glyphs (such as '1', 'l', and 'I'), and approachable warmth for medical disclaimers, triage notes, and user instructions.

### Numerics & Queue Counters
Numbers on queue tickets and live estimated time indicators must use tabular figures (`font-variant-numeric: tabular-nums`) to prevent layout shifts as counters tick down.

## Layout & Spacing

The layout is optimized for single-handed mobile usage on Android devices, anchoring primary progress indicators and primary actions within the lower thumb zones.

### Grid Architecture
- **Mobile Grid:** 4-column fluid layout with `16px` (`1rem`) gutters and `16px` outer margins.
- **Dense/Compact Displays:** Drops to `12px` (`0.75rem`) margins while retaining a minimum interactive target of `48px` on all tap boundaries.
- **Tablet / Large Mobile Foldables:** Expands to an 8-column layout with `24px` margins and a maximum content column constraint of `640px` for patient ticket previews.

### Spacing Scale & Rhythm
- Spacing follows an absolute 4px/8px modular rhythm.
- Vertical stack spacing inside cards adheres strictly to `space-sm` (`8px`) for tight related metadata (e.g., Doctor name + specialty) and `space-md` (`16px`) to isolate status indicators from functional buttons.

## Elevation & Depth

Visual hierarchy uses **tonal elevation accompanied by soft ambient shadows** tinted with the Tertiary Midnight Navy hue to avoid muddy grayscale shadows.

### Surface Tiers
- **Surface Level 0 (Canvas):** Mint Light Surface (`#F0F7F4`), fully flat, serving as the backdrop for all screens.
- **Surface Level 1 (Resting Cards & Navigation Bars):** Crisp White (`#FFFFFF`) with an ambient shadow: `0px 2px 8px rgba(22, 38, 74, 0.04)`.
- **Surface Level 2 (Active Queue Ticket):** Crisp White (`#FFFFFF`) paired with an ambient tinted shadow: `0px 8px 24px rgba(30, 122, 70, 0.08)` and a faint outline of `rgba(30, 122, 70, 0.16)`.
- **Surface Level 3 (Modals, Bottom Sheets & Sticky Action Bars):** Crisp White (`#FFFFFF`) with `0px 12px 32px rgba(22, 38, 74, 0.12)`.

### Live Pulse Effect
Queue cards that are actively in progress feature an animated beacon: an emerald pulse ring (`rgba(30, 122, 70, 0.2)`) that radiates outward 6px beyond the badge perimeter to convey live network connectivity.

## Shapes

The roundedness language is calibrated to Level 2 (Material 3 Expressive Standard), generating smooth, tactile corners that reduce clinical anxiety.

- **Queue Ticket Containers:** `16px` (`rounded-lg`) corner radii, with an optional inward circular cutout (`12px` radius) on the horizontal dividing seam to visually reference physical clinical dispenser stubs.
- **Buttons and Primary Chips:** Full-pill treatment (`9999px`) for quick-filter categories and high-frequency tap targets to maximize thumb comfort.
- **Bottom Sheets and Dialogs:** `24px` (`rounded-xl`) top corner radii, emphasizing soft containment.
- **Status Badges & Live Dots:** Soft structural rounding (`8px`) or pure geometric circles for pulse points.

## Components

### Buttons
- **Primary CTA (Forest Green):** Background `#1E7A46`, text `#FFFFFF`, height `48px`, border radius `9999px`. State changes: hover/focus introduces an inset light tint overlay (`rgba(255, 255, 255, 0.12)`).
- **Secondary Action (Warm Terracotta):** Background `#C1541A`, text `#FFFFFF`, used for "Check In Now", "Call Desk", or "Cancel Queue".
- **Surface Action (Tonal):** Background `rgba(30, 122, 70, 0.08)`, text `#1E7A46`, zero border, used for secondary appointment rescheduling.

### Live Queue Ticket
- **Layout:** Two-tier card. Upper segment displays clinic name, attending specialist, and the tabular token counter (e.g., `#B-24`). Lower tier displays wait duration, people ahead, and room number.
- **Seam:** Perforated horizontal hairline divider (`#E2E8F0`) with subtle semi-circle cutouts at both edges.
- **Counters:** Numerals styled in `Manrope Display-lg` (`44px`, 800 weight) colored in Midnight Navy (`#16264A`).

### Status Badges & Pulse Indicators
- **"Your Turn" / Calling Badge:** Terracotta solid fill (`#C1541A`) with bold white text and a synchronous outer pulse animation.
- **"In Waiting" Badge:** Mint tint fill (`#E0F2E9`), forest green label (`#1E7A46`), featuring an embedded 6px solid emerald dot.
- **"Delayed / Rescheduled":** Soft amber fill (`#FEF3C7`), dark slate label (`#78350F`).

### Material 3 Filter Chips
- **Selected:** Solid Primary Green fill (`#1E7A46`), text `#FFFFFF`, with a left-aligned checkmark icon.
- **Unselected:** Background `#FFFFFF`, border `1px solid #CBD5E1`, text `#1E293B`.
- **Dimensions:** Height `32px`, corner radius `8px`, horizontal interior padding `12px`.

### Form Fields & Inputs
- Material 3 outlined container style with an unfocused border of `#CBD5E1` and an active focus ring of `2px solid #1E7A46`.
- Background defaults to `#FFFFFF` with floating labels styled in `Plus Jakarta Sans Label-md`.

### Lists & Appointment Row Items
- White card items on Mint canvas, separated by `space-sm` margins.
- Leading avatar/icon box uses rounded `12px` geometry with subtle clinical icon glyphs.
- Trailing chevron or status pill aligned right with fixed width to eliminate jagged eye tracking down the list.