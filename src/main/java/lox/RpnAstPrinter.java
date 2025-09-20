package lox;

public class RpnAstPrinter implements Expr.Visitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
        return rpn(binary.operator.lexeme, binary.left, binary.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
        return rpn("group", grouping.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
        return literal.value == null ? "nil" : literal.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary unary) {
        return rpn(unary.operator.lexeme, unary.right);
    }

    private String rpn(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        for(Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }

        builder.append(name);
        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Binary(
                        new Expr.Literal(1),
                        new Token(TokenType.PLUS, "+", null, 1),
                        new Expr.Literal(2)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Binary(
                        new Expr.Literal(4),
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(3)
                )
        );
        RpnAstPrinter printer = new RpnAstPrinter();
        System.out.println(printer.print(expression));
    }
}
