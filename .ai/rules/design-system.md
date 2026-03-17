# Design System Tokens

## 🎨 Color Palette
| Token | Value | Description |
| :--- | :--- | :--- |
| `--color-primary` | `#197fe6` | Brand primary color (from base system) |
| `--color-dark` | `#212529` | Background dark color (Bootstrap bg-dark) |
| `--color-white` | `#ffffff` | Pure white |
| `--color-gray-100` | `#f8f9fa` | Lightest gray (Bootstrap bg-light) |
| `--color-text-main` | `#212529` | Primary text color |
| `--color-text-muted` | `#6c757d` | Muted text color |
| `--color-text-inverse` | `#ffffff` | Text on dark backgrounds |

## 📐 Spacing (Bootstrap 5 Scales)
| Token | Value | Description |
| :--- | :--- | :--- |
| `--spacing-xs` | `0.25rem` | `mt-1`, `p-1` (4px) |
| `--spacing-sm` | `0.5rem` | `mt-2`, `p-2` (8px) |
| `--spacing-md` | `1rem` | `mt-3`, `p-3` (16px) |
| `--spacing-lg` | `1.5rem` | `mt-4`, `p-4` (24px) |
| `--spacing-xl` | `3rem` | `mt-5`, `p-5` (48px) |

## 💎 Shape & Elevation
| Token | Value | Description |
| :--- | :--- | :--- |
| `--radius-sm` | `8px` | Subtle rounding |
| `--radius-base` | `12px` | Standard rounding (from base system) |
| `--radius-lg` | `16px` | Pronounced rounding |
| `--shadow-base` | `0 4px 20px rgba(0,0,0,0.05)` | Default soft shadow |

## 🖋️ Typography
| Token | Value | Description |
| :--- | :--- | :--- |
| `--font-family-base` | `'Plus Jakarta Sans', 'Pretendard', sans-serif` | Modern sans-serif stack |
| `--font-weight-regular` | `400` | Base weight |
| `--font-weight-medium` | `500` | Medium emphasis |
| `--font-weight-bold` | `700` | Strong emphasis |

## 🛠️ Global CSS Variables (:root)
```css
:root {
  /* Colors */
  --color-primary: #197fe6;
  --color-dark: #212529;
  --color-white: #ffffff;
  --color-gray-100: #f8f9fa;
  --color-text-main: #212529;
  --color-text-muted: #6c757d;
  --color-text-inverse: #ffffff;

  /* Spacing */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 3rem;

  /* Shape & Elevation */
  --radius-sm: 8px;
  --radius-base: 12px;
  --radius-lg: 16px;
  --shadow-base: 0 4px 20px rgba(0,0,0,0.05);

  /* Typography */
  --font-family-base: 'Plus Jakarta Sans', 'Pretendard', sans-serif;
}
```
