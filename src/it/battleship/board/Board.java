package it.battleship.board;

import it.battleship.board.exceptions.BoardException;
import it.battleship.board.exceptions.PositionException;
import it.battleship.ships.Ship;
import it.battleship.ships.utils.Direction;
import java.util.Arrays;

public class Board {
    private final int length; //one variable for rows = columns = 10 [10x10 matrix]
    private char[][] board;
    private int numShips = 0;
    private int numHits = 0;
    public static final char HIT = '☒';
    public static final char MISS = '☸';
    public static final char SHIP = '☐';
    public static final char WATER = '~';

    public Board(int length){
        this.length = length;
        board = initBoard();
    }
    public Board(char[][] matrix){
        this.length = matrix.length;
        board = matrix;
    }

    private char[][] initBoard(){
        char[][] matrix = new char[length][length];
        for (char[] row: matrix){
            Arrays.fill(row, WATER);
        }
        return matrix;
    }

    public int getLength() {
        return length;
    }
    public int getNumShips() {
        return numShips;
    }
    public char[][] getBoard() {
        return board;
    }

    public char at(Position position) {
        return board[position.getRow()][position.getColumn()];
    }
    public boolean set(char status, Position position) {
        board[position.getRow()][position.getColumn()] = status;
        return true;
    }

    /**
     * Metodo per controllare se in una posizione specifica è presente una nave
     * @param position Posizione della nave
     * @return Valore booleano se è presente una nave in una posizione specifica
     */
    public boolean thereIsShip(Position position) {
        return at(position) == SHIP;
    }
    public boolean thereIsWater(Position position) {
        return at(position) == WATER;
    }
    public boolean thereIsMiss(Position position){
        return at(position) == MISS;
    }
    public boolean thereIsHit(Position position){
        return at(position) == HIT;
    }


    public boolean thereIsSpace(Ship ship) {
        int l = ship.getLength();
        int x = ship.getPosition().getRow();
        int y = ship.getPosition().getColumn();
        if (ship.getDirection() == Direction.HORIZONTAL) return (length - y + 1) > l;
        else return (length - x + 1) > l;
    }
    public boolean isNearShip(Ship ship) throws PositionException {
        int k, row, column;
        row = ship.getPosition().getRow();
        column = ship.getPosition().getColumn();

        if (ship.getDirection() == Direction.HORIZONTAL) k = column;
        else k = row;

        for (int i = 0; i < ship.getLength() && k + i < length - 1; i++) {
            if (isShipAround(row, column)) return true;

            if (ship.getDirection() == Direction.HORIZONTAL) column++;
            else if (ship.getDirection() == Direction.VERTICAL) row++;
        }
        return false;
    }
    private boolean isShipAround(int row, int column) throws PositionException {
        if (row - 1 >= 0){
            if(thereIsShip(new Position(row - 1, column))) return true;
        }
        if (column - 1 >= 0){
            if(thereIsShip(new Position(row, column - 1))) return true;
        }
        if (row - 1 >= 0 && column - 1 >= 0){
            if(thereIsShip(new Position(row - 1, column - 1))) return true;
        }
        if (row + 1 < length && column - 1 >= 0){
            if(thereIsShip(new Position(row + 1, column - 1))) return true;
        }
        if (row + 1 < length){
            if(thereIsShip(new Position(row + 1, column))) return true;
        }
        if (row + 1 < length && column + 1 < length){
            if(thereIsShip(new Position(row + 1, column + 1))) return true;
        }
        if (column + 1 < length){
            if(thereIsShip(new Position(row, column + 1))) return true;
        }
        if (row - 1 >= 0 && column + 1 < length){
            if(thereIsShip(new Position(row - 1, column + 1))) return true;
        }
        return false;
    }

    public boolean addShip(Ship ship) throws BoardException, PositionException {
        int k = 0, row, column;
        if (!thereIsShip(ship.getPosition())){
            if (thereIsSpace(ship)){
                if (!isNearShip(ship)){
                    row = ship.getPosition().getRow();
                    column = ship.getPosition().getColumn();
                    for (int i = 0; i < ship.getLength() && k + i < length; i++) {
                        if (ship.getDirection() == Direction.HORIZONTAL) {
                            if (i == 0) k = column;
                            board[row][column + i] = SHIP;
                        }
                        else if (ship.getDirection() == Direction.VERTICAL) {
                            if (i == 0) k = row;
                            board[row + i][column] = SHIP;
                        }
                    }
                    numShips++;
                    return true;
                }
                else throw new BoardException("Errore, un'altra nave si trova nelle vicinanze");
            }
            else throw new BoardException("Errore, non c'è spazio per quella nave con quella direzione");
        }
        else throw new BoardException("Errore, è già una nave in quella posizione");
    }

    public boolean addHit(Position position) throws BoardException {
        if (thereIsShip(position)) {
            numHits++;
            return set(HIT, position);
        }
        else if (thereIsWater(position)) return set(MISS, position);
        else throw new BoardException("Errore, hai già sparato in questa posizione");
    }
    public int countHits(){
        return numHits;
    }

    public Board getBoardHideShips() throws PositionException{
        char[][] matrix = new char[length][length];
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                if (!thereIsShip(new Position(i,j))){
                    matrix[i][j] = at(new Position(i,j));
                }
                else matrix[i][j] = WATER;
            }
        }
        return new Board(matrix);
    }

    public void reset(){
        numShips = 0;
        board = initBoard();
    }

}
