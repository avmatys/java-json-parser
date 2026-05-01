package json;

public interface JSONTokener {
    public Token peek();
    public Token consume();
    public boolean hasMore();
}
