package org.everit.persistence.querydsl.dtoquery;

import java.util.List;

import javax.annotation.Nullable;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.HashCodeVisitor;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import com.querydsl.core.types.Visitor;

public class QDTOWithKeys<T>
    implements Expression<DTOWithKeys<T>>, FactoryExpression<DTOWithKeys<T>> {

  private static final long serialVersionUID = -8947639515013490017L;

  @Nullable
  private transient volatile Integer hashCode;

  @Nullable
  private transient volatile String toString;

  public QDTOWithKeys(FactoryExpression<T> beanExpression, Expression<?>... keys) {

    // TODO Auto-generated constructor stub
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }

  @Override
  public List<Expression<?>> getArgs() {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends DTOWithKeys<T>> getType() {
    return (Class<? extends DTOWithKeys<T>>) DTOWithKeys.class;
  }

  @Override
  public final int hashCode() {
    if (this.hashCode == null) {
      this.hashCode = accept(HashCodeVisitor.DEFAULT, null);
    }
    return this.hashCode;
  }

  @Override
  public DTOWithKeys<T> newInstance(Object... args) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public final String toString() {
    if (this.toString == null) {
      this.toString = accept(ToStringVisitor.DEFAULT, Templates.DEFAULT);
    }
    return this.toString;
  }

}
