import katex from 'katex'
import 'katex/dist/katex.min.css'

export function useMathRenderer() {
  /**
   * Escape HTML entities to prevent XSS
   */
  const escapeHtml = (text) => {
    const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
    }
    return text.replace(/[&<>"']/g, m => map[m])
  }

  /**
   * Unescape HTML entities for KaTeX processing
   */
  const unescapeHtml = (text) => {
    return text.replace(/&amp;/g, '&')
      .replace(/&lt;/g, '<')
      .replace(/&gt;/g, '>')
      .replace(/&quot;/g, '"')
      .replace(/&#039;/g, "'")
  }

  /**
   * Render math expressions with KaTeX
   * Supports multiple LaTeX delimiters:
   * - \[...\] for display math (block)
   * - $$...$$ for display math (block)
   * - \(...\) for inline math
   * - $...$ for inline math
   */
  const renderMath = (text) => {
    if (!text) return ''
    
    // Escape HTML first to prevent XSS
    let result = escapeHtml(text)
    
    // 1. Replace LaTeX display math \[ ... \] (process first to avoid conflicts)
    result = result.replace(/\\\[([\s\S]*?)\\\]/g, (match, formula) => {
      try {
        const unescapedFormula = unescapeHtml(formula)
        return katex.renderToString(unescapedFormula.trim(), {
          displayMode: true,
          throwOnError: false
        })
      } catch (e) {
        console.error('KaTeX render error for \\[...\\]:', e)
        return match
      }
    })
    
    // 2. Replace block math $$ ... $$
    result = result.replace(/\$\$([\s\S]*?)\$\$/g, (match, formula) => {
      try {
        const unescapedFormula = unescapeHtml(formula)
        return katex.renderToString(unescapedFormula.trim(), {
          displayMode: true,
          throwOnError: false
        })
      } catch (e) {
        console.error('KaTeX render error for $$...$$:', e)
        return match
      }
    })
    
    // 3. Replace LaTeX inline math \( ... \)
    result = result.replace(/\\\((.*?)\\\)/g, (match, formula) => {
      try {
        const unescapedFormula = unescapeHtml(formula)
        return katex.renderToString(unescapedFormula.trim(), {
          displayMode: false,
          throwOnError: false
        })
      } catch (e) {
        console.error('KaTeX render error for \\(...\\):', e)
        return match
      }
    })
    
    // 4. Replace inline math $ ... $
    result = result.replace(/\$(.*?)\$/g, (match, formula) => {
      try {
        const unescapedFormula = unescapeHtml(formula)
        return katex.renderToString(unescapedFormula.trim(), {
          displayMode: false,
          throwOnError: false
        })
      } catch (e) {
        console.error('KaTeX render error for $...$:', e)
        return match
      }
    })
    
    // Handle common text patterns (fallback if no delimiters)
    if (!result.includes('katex')) {
      // Replace sqrt(x) with √x
      result = result.replace(/sqrt\(([^)]+)\)/g, '√($1)')
      
      // Replace subscripts z_1 -> z₁
      result = result.replace(/([a-zA-Z])_(\d+)/g, (match, letter, num) => {
        const subscripts = ['₀', '₁', '₂', '₃', '₄', '₅', '₆', '₇', '₈', '₉']
        return letter + num.split('').map(d => subscripts[parseInt(d)]).join('')
      })
      
      // Replace superscripts x^2 -> x²
      result = result.replace(/([a-zA-Z0-9])\^(\d+)/g, (match, base, exp) => {
        const superscripts = ['⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹']
        return base + exp.split('').map(d => superscripts[parseInt(d)]).join('')
      })
    }
    
    return result
  }

  /**
   * Auto-detect and wrap math expressions with $ delimiters
   * Useful when AI returns formulas without proper delimiters
   */
  const autoWrapMath = (text) => {
    if (!text) return ''
    
    let result = text
    
    // Wrap expressions with |...| (absolute value)
    result = result.replace(/\|([^|]+)\|/g, (match, content) => {
      if (content.includes('_') || content.includes('^') || content.includes('\\')) {
        return `$${match}$`
      }
      return match
    })
    
    // Wrap expressions with sqrt
    result = result.replace(/sqrt\([^)]+\)/g, (match) => {
      if (!match.startsWith('$')) {
        return `$\\${match.replace('sqrt', 'sqrt')}$`
      }
      return match
    })
    
    return result
  }

  return {
    renderMath,
    autoWrapMath
  }
}
