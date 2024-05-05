package main.visitor.nameAnalyzer;

import main.ast.nodes.Program;
import main.ast.nodes.declaration.FunctionDeclaration;
import main.ast.nodes.declaration.PatternDeclaration;
import main.ast.nodes.expression.*;
import main.ast.nodes.statement.*;
import main.compileError.CompileError;
import main.compileError.nameErrors.CircularDependency;
import main.symbolTable.SymbolTable;
import main.visitor.Visitor;
import main.symbolTable.utils.Graph;

import java.util.ArrayList;
import java.util.List;

public class DependencyDetector extends Visitor<Void> {
    private String funcName;
    public ArrayList<CompileError> dependencyError = new ArrayList<>();
    private Graph dependencyGraph = new Graph();
    @Override
    public Void visit(Program program){
        for(FunctionDeclaration functionDeclaration : program.getFunctionDeclarations()){
            if (functionDeclaration != null) {
                functionDeclaration.accept(this);
            }
        }

        return null;
    }
    @Override
    public Void visit(ForStatement forStatement) {
        for (var expr : forStatement.getRangeExpressions()) {
            if (expr != null) {
                expr.accept(this);
            }
        }

        for (var expr : forStatement.getLoopBodyExpressions()) {
            if (expr != null) {
                expr.accept(this);
            }
        }

        for (var stmt_ : forStatement.getLoopBody()) {
            if (stmt_ != null){
                stmt_.accept(this);
            }
        }

        var stmt_ = forStatement.getReturnStatement();
        if (stmt_ != null) {
            if (stmt_ != null) {
                stmt_.accept(this);
            }
        }
        return null;
    }
    @Override
    public Void visit(IfStatement ifStatement) {
        for (var condition : ifStatement.getConditions()) {
            if (condition != null) {
                condition.accept(this);
            }
        }

        for (var stmt_ : ifStatement.getThenBody()) {
            if (stmt_ != null) {
                stmt_.accept(this);
            }
        }

        for (var stmt_ : ifStatement.getElseBody()) {
            if (stmt_ != null) {
                stmt_.accept(this);
            }
        }
        return null;
    }
    @Override
    public Void visit(LoopDoStatement loopDoStatement) {
        for (var condition : loopDoStatement.getLoopConditions()) {
            if (condition != null) {
                condition.accept(this);
            }
        }

        for (var stmt_ : loopDoStatement.getLoopBodyStmts()) {
            if (stmt_ != null) {
                stmt_.accept(this);
            }
        }
        var stmt_ = loopDoStatement.getLoopRetStmt();
        if (stmt_ != null) {
            stmt_.accept(this);
        }
        return null;
    }
    @Override
    public Void visit(AssignStatement assignStmt) {
        if (assignStmt.getAssignExpression() != null) {
            assignStmt.getAssignExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(ExpressionStatement expressionStatement) {
        if (expressionStatement.getExpression() != null) {
            expressionStatement.getExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(PushStatement pushStatement) {
        if (pushStatement.getToBeAdded() != null) {
            pushStatement.getToBeAdded().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(PutStatement putStatement) {
        if (putStatement.getExpression() != null) {
            putStatement.getExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(ReturnStatement returnStatement) {
        var retExpr = returnStatement.getReturnExp();
        if (retExpr != null) {
            returnStatement.getReturnExp().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(AccessExpression accessExpression) {
        Identifier id = (Identifier) accessExpression.getAccessedExpression();
        dependencyGraph.addEdge(funcName, id.getName());


        for (var arg : accessExpression.getArguments()) {
            if (arg != null) {
                arg.accept(this);
            }
        }

        for (var dimAccess : accessExpression.getDimentionalAccess()) {
            if (dimAccess != null) {
                dimAccess.accept(this);
            }
        }

        return null;
    }
    @Override
    public Void visit(UnaryExpression unaryExpression) {
        if (unaryExpression.getExpression() != null) {
            unaryExpression.getExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(MatchPatternStatement matchPatternStatement) {
        if (matchPatternStatement.getMatchArgument() != null) {
            matchPatternStatement.getMatchArgument().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(BinaryExpression binaryExpression) {
        if (binaryExpression.getFirstOperand() != null) {
            binaryExpression.getFirstOperand().accept(this);
        }
        if (binaryExpression.getSecondOperand() != null) {
            binaryExpression.getSecondOperand().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(LenStatement lenStatement) {
        if (lenStatement.getExpression() != null) {
            lenStatement.getExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(ChopStatement chopStatement) {
        if (chopStatement.getChopExpression() != null) {
            chopStatement.getChopExpression().accept(this);
        }
        return null;
    }
    @Override
    public Void visit(ChompStatement chompStatement) {
        if (chompStatement.getChompExpression() != null) {
            if (chompStatement.getChompExpression() != null) {
                chompStatement.getChompExpression().accept(this);
            }
        }
        return null;
    }
    @Override
    public Void visit(AppendExpression appendExpression) {
        for (var appended : appendExpression.getAppendeds()) {
            if (appended != null) {
                appended.accept(this);
            }
        }
//        appendExpression.getAppendee();
        return null;
    }
    @Override
    public Void visit(PatternDeclaration patternDeclaration) {
//        patternDeclaration.getTargetVariable();
//        patternDeclaration.getReturnExp();
//        patternDeclaration.getConditions();
        return null;
    }
    @Override
    public Void visit(FunctionDeclaration functionDeclaration){
        funcName = functionDeclaration.getFunctionName().getName();
        for (var stmt : functionDeclaration.getBody()) {
            if (stmt != null) {
                stmt.accept(this);
            }
        }
        return null;
    }
    public Void findDependency(){
        ArrayList<List<String>> cycles =  dependencyGraph.findCycles();
        for (List<String> cycle : cycles) {
            dependencyError.add(new CircularDependency(cycle));
        }
        return null;
    }
}



//public Void visit(Identifier identifier)
//
//public Void visit(VarDeclaration varDeclaration)
//
//public Void visit(LambdaExpression lambdaExpression)

