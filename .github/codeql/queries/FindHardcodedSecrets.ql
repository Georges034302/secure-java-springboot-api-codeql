/**
 * @name Find hardcoded secrets
 * @description Detects hardcoded secrets in code
 * @kind problem
 * @problem.severity warning
 * @security-severity 8.0
 * @id java/hardcoded-secrets
 * @tags security
 */

import java

from StringLiteral literal
where
  exists(Field field |
    // Match field name patterns (case-insensitive)
    field.getName().regexpMatch("(?i).*(api_?key|token|secret|password).*") and
    // Match field initialization - this links the literal to the field
    literal = field.getInitializer() and
    // Match common secret patterns in the literal's value
    literal.getValue().regexpMatch("(?i).*(sk_.*|apikey_.*|token_.*|[a-zA-Z0-9+/=]{32,})")
  )
select
  literal,
  "Hardcoded secret detected: '" + literal.getValue() + "'"