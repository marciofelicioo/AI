package Exceptions;

public class PlatformException extends Exception{
    private static final long serialVersionUID = 8L;
    public PlatformException(){
        super();
    }
    public PlatformException(String mensagem)
    {
        super(mensagem);
    }
}
