import katex from 'katex'
import 'katex/dist/katex.min.css'

export function useMathRenderer() {
  /**
   * Render inline math expressions wrapped in $ ... $
   * Render block math expressions wrapped in $$ ... $$
   * Also handle common text patterns like sqrt(x) -> √x
   */
  const renderMath = (text) => {
    if (!text) return ''
    
    let result = text
    
    // Replace block math $$ ... $$
    result = result.replace(/\$\$(.*?)\$\$/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: true,
          throwOnError: false
        })
      } catch (e) {
        return match
      }
    })
    
    // Replace inline math $ ... $
    result = result.replace(/\$(.*?)\$/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: false,
          throwOnError: false
        })
      } catch (e) {
        return match
      }
    })
    
    // Handle common text patterns (fallback if no $ delimiters)
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
    result = result.replace(/sqrt\([^)]+\)/g, match => {
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
