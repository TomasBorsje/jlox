package lox;

public class Interpreter implements Expr.Visitor<Object> {

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary unary) {
        Object right = evaluate(unary.right);

        switch (unary.operator.type) {
            case MINUS:
                checkNumberOperand(unary.operator, right);
                return -(double) right;
            case BANG:
                return !isTruthy(right);
        }

        // This is unreachable.
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping grouping) {
        return evaluate(grouping.expression);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary binary) {
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);

        switch (binary.operator.type) {
            case GREATER:
                checkNumberOperands(binary.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(binary.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(binary.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(binary.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(binary.operator, left, right);
                return (double) left - (double) right;
            case PLUS: {
                if (left instanceof Double leftNumber && right instanceof Double rightNumber) {
                    return leftNumber + rightNumber;
                }
                if (left instanceof String leftString && right instanceof String rightString) {
                    return leftString + rightString;
                }
                // String + non-string concatenation
                if (left instanceof String leftString) {
                    return leftString + stringify(right);
                }
                if (right instanceof String rightString) {
                    return stringify(left) + rightString;
                }
                throw new RuntimeError(binary.operator, "Operands must be two numbers or two strings.");
            }
            case SLASH:
                checkNumberOperands(binary.operator, left, right);
                checkNonZeroOperand(binary.operator, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(binary.operator, left, right);
                return (double) left * (double) right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void checkNonZeroOperand(Token operator, Object operand) {
        if(operand instanceof Double number && number != 0) {
            return;
        }
        throw new RuntimeError(operator, "Operand must not be 0.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Null and false booleans are falsey, everything else is truthy.
     */
    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean bool) {
            return bool;
        }
        return true;
    }
}
