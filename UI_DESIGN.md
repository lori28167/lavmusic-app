# Ticly Lavamusic UI Design Document

## Overview
The Ticly Lavamusic application features a modern Material Expressive design with a clean, functional layout optimized for music playback and management.

## Color Scheme (Material Design)
- **Primary Color**: Purple (#6200EE)
- **Primary Variant**: Dark Purple (#3700B3)
- **Secondary Color**: Teal (#03DAC6)
- **Background**: Light Gray (#FAFAFA)
- **Surface**: White (#FFFFFF)
- **Error**: Red (#B00020)

## Layout Structure

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃                      Ticly Lavamusic                               ┃  ← App Bar (Purple)
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃                                                                     ┃
┃  ┌──────────────────────────┐  ┌──────────────────────────┐      ┃
┃  │ Search Music             │  │ Queue                 🗑  │      ┃
┃  ├──────────────────────────┤  ├──────────────────────────┤      ┃
┃  │                          │  │                          │      ┃
┃  │ [Search field]  [Search] │  │ ♪ Sample Song 1          │      ┃
┃  │                          │  │   Artist A • 3:00        │      ┃
┃  │ Results                  │  │                          │      ┃
┃  │ ──────────────────────── │  │ ♪ Sample Song 2          │      ┃
┃  │ ♪ Sample Song 1      [+] │  │   Artist B • 3:30        │      ┃
┃  │   Artist A • 3:00        │  │                          │      ┃
┃  │                          │  │ ♪ Sample Song 3          │      ┃
┃  │ ♪ Sample Song 2      [+] │  │   Artist C • 3:15        │      ┃
┃  │   Artist B • 3:30        │  │                          │      ┃
┃  │                          │  │                          │      ┃
┃  │ ♪ Sample Song 3      [+] │  │                          │      ┃
┃  │   Artist C • 3:15        │  │                          │      ┃
┃  │                          │  │                          │      ┃
┃  └──────────────────────────┘  └──────────────────────────┘      ┃
┃                                                                     ┃
┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃  Now Playing: Sample Song Title                                    ┃
┃  Artist Name                                                        ┃
┃                                                                     ┃
┃  0:00 ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬○─────────── 3:00      ┃
┃                                                                     ┃
┃              ⏮      ( ▶ )      ⏭           🔊 ▬▬▬▬○────            ┃
┃           Previous  Play/Pause Next          Volume                ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

## Component Details

### 1. App Bar (Top)
- Background: Primary Purple (#6200EE)
- Text: White
- Height: 64px
- Contains: Application title "Ticly Lavamusic" in bold, 24px font
- Style: Flat with slight elevation shadow

### 2. Main Content Area (Center)

#### Left Panel: Search & Results
- Background: White surface with elevation shadow
- Border Radius: 16px
- Padding: 20px
- Width: 50% of window

**Components:**
- **Header**: "Search Music" in bold, 18px
- **Search Box**: 
  - Text field with placeholder "Enter song name or artist..."
  - Rounded corners (8px radius)
  - Teal "Search" button
- **Results List**:
  - Scrollable list view
  - Each item shows:
    - Song title (bold, 13px)
    - Artist and duration (gray, 11px)
    - "+" button (teal) to add to queue
  - Hover effect on items

#### Right Panel: Queue
- Background: White surface with elevation shadow
- Border Radius: 16px
- Padding: 20px
- Width: 50% of window

**Components:**
- **Header**: "Queue" in bold, 18px with trash icon (🗑) for clear
- **Queue List**:
  - Scrollable list view
  - Shows queued tracks in order
  - Each item displays:
    - Song title (bold, 13px)
    - Artist and duration (gray, 11px)
  - Visual indication of current track

### 3. Player Controls (Bottom)
- Background: White with top border
- Height: 140px
- Padding: 20px

**Components:**
- **Track Info**:
  - Current track title (bold, 16px)
  - Artist name (gray, 12px)

- **Progress Bar**:
  - Current time label (left)
  - Slider for track progress
  - Total duration label (right)
  - Rounded slider with teal accent

- **Control Buttons**:
  - Previous track (⏮) - 24px icon
  - Play/Pause (▶/⏸) - Large circular button (60x60px) with purple background
  - Next track (⏭) - 24px icon
  - All buttons have hover effects

- **Volume Control**:
  - Speaker icon (🔊)
  - Horizontal slider (0-100)
  - Positioned on the right side
  - Teal accent color

## Interaction Design

### Hover Effects
- Buttons: Light gray background on hover
- List items: Subtle background color change
- Play/Pause button: Darker shade of purple on hover

### Transitions
- Smooth color transitions (200ms)
- Elevation changes on interaction
- Slider animations

### Responsive Behavior
- Minimum window size: 900x650px
- Panels adjust to window width
- Lists scroll independently
- Player controls remain fixed at bottom

## Typography
- **Font Family**: System default (Sans-serif)
- **Title**: 24px Bold
- **Headers**: 18px Bold
- **Body**: 13px Regular
- **Secondary text**: 11px Regular
- **Icons/Symbols**: 18-24px

## Accessibility
- High contrast between text and backgrounds
- Clear visual hierarchy
- Tooltips on icon-only buttons
- Keyboard navigation support
- Screen reader compatible labels

## Material Design Principles Applied
1. **Elevation**: Cards use subtle shadows for depth
2. **Motion**: Smooth transitions and hover effects
3. **Typography**: Clear hierarchy with multiple font weights
4. **Color**: Consistent use of primary and secondary colors
5. **Iconography**: Simple, recognizable symbols
6. **Layout**: Generous whitespace and padding
7. **Components**: Rounded corners and modern controls
