package com.example.diploma.main.Clothes;

public abstract class Clothes {
    abstract public Type getType();
}

enum Type{
    TOP,
    BOTTOM,
    DRESSEDUP,
    SHOES
}
