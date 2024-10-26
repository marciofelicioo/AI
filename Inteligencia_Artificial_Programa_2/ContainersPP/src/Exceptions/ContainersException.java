package Exceptions;

public class ContainersException extends Exception{
  private static final long serialVersionUID = 8L;
  public ContainersException(){
    super();
  }
  public ContainersException(String mensagem)
  {
    super(mensagem);
  }
}
