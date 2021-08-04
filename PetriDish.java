/** 
 * Name: Matthew Bernard
 * Email: mibernar@ucsd.edu
 * 
 * This program describes a petridish which can hold all the cells
 * described by the other classes. This class does not inherit anything.
 */


import java.util.*;


/** 
 * This class contains three instance variables, one constructor, and
 * eight methods.
 */
public class PetriDish {

    //Constant vars used to output the petridish    
    private static final String VERTICAL_BAR = "|";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STRING = " ";

    public Cell[][] dish;
    public List<Movable> movables;
    public List<Divisible> divisibles;
    
    /**
     * Parameterized constructor which creates the petridish/filling in the 
     * 2d array. Also initializes the movable and divisible lists.
     * @param board the 2d array on strings used the fill in the dish
     */
    public PetriDish(String[][] board) {
        
        dish = new Cell[board.length][board[0].length];

        movables = new ArrayList<Movable>();
        divisibles = new ArrayList<Divisible>();

        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(board[i][j] == "null") {
                    continue;
                }

                String[] splitString = board[i][j].split(" ");

                switch(splitString[0]) {
                    case "CellStationary": 
                        dish[i][j] = new CellStationary(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;
                    case "CellMoveUp": 
                        dish[i][j] = new CellMoveUp(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;
                    case "CellDivide": 
                        dish[i][j] = new CellDivide(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;
                    case "CellMoveToggle": 
                        dish[i][j] = new CellMoveToggle(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;
                    case "CellMoveDiagonal": 
                        dish[i][j] = new CellMoveDiagonal(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;
                    case "CellMoveToggleChild": 
                        dish[i][j] = new CellMoveToggleChild(i, j, 
                                Integer.parseInt(splitString[1]));
                        break;

                }

                if(dish[i][j] instanceof Movable){
                    movables.add((Movable)dish[i][j]);
                }
                else if(dish[i][j] instanceof Divisible){
                    divisibles.add((Divisible)dish[i][j]);
                }

            }
        }

    }

    /**
     * Function used to output the petridish for visualizing
     * @return the string which displays the petridish
     */
    public String toString() { //optional
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < dish.length; i++) {
            sb.append(VERTICAL_BAR);
            for(int j = 0; j < dish[0].length; j++){
                sb.append(dish[i][j] == null ? EMPTY_STRING : 
                        dish[i][j].toString());
                sb.append(VERTICAL_BAR);
            }
            sb.append(NEW_LINE);
            //sb.append(HORIZONTAL_BARS);
        }
        return sb.toString();
    } 
    
    /**
     * Helper function used to deal with wrapping when number are out of bounds 
     * @param pos an array containing a row and column 
     */
    private void handleWrap(int[] pos){
        // handle first position
        // If the row position < 0 it should be set to the last row in dish
        if(pos[0] < 0){
          pos[0] = dish.length - 1;
        }
        // If the row position > the last row in dish it should be set to 0
        if(pos[0] > dish.length - 1){
          pos[0] = 0;
        }
    
        // handle second position
        // If the col position < 0 it should be set to the last col in dish
        if(pos[1] < 0){
          pos[1] = dish[0].length - 1;
        }
        // If the col position > the last col in dish it should be set to 0
        if(pos[1] > dish[0].length - 1){
          pos[1] = 0;
        }
    }

    /**
     * Helper function used to individually add a neighbor of a cell
     * to a list
     * @param row the row of the cell being checked
     * @param col the column of the cell being checked
     * @param neighbors the list of neighbors being added to
     */
    private void addNeighbor(int row, int col, List<Cell> neighbors) {
        int[] cellPosition = new int[2];     

        cellPosition[0] = row;
        cellPosition[1] = col;

        handleWrap(cellPosition);
        
        if(dish[cellPosition[0]][cellPosition[1]] != null)
            neighbors.add(dish[cellPosition[0]][cellPosition[1]]);
    }

    /**
     * Function used to find all the neighbors of a certain cell on the dish
     * @param row the row of the cell being checked
     * @param col the column of the cell being checked
     * @return the list of neighbors of the cell
     */
    public List<Cell> getNeighborsOf(int row, int col) {

        if(row >= dish.length || row < 0 || col >= dish[0].length || col < 0) {
            return null;
        }

        List<Cell> neighbors = new ArrayList<Cell>();

        
        addNeighbor(row - 1, col - 1, neighbors);
        addNeighbor(row - 1, col, neighbors);
        addNeighbor(row - 1, col + 1, neighbors);
        addNeighbor(row, col - 1, neighbors);
        addNeighbor(row, col + 1, neighbors);
        addNeighbor(row + 1, col - 1, neighbors);
        addNeighbor(row + 1, col, neighbors);
        addNeighbor(row + 1, col + 1, neighbors);


        return neighbors;
    }

    /**
     * Function used to get the move of every movable cell on the petridish
     * and move them
     */
    public void move() {

        List<CellMoveUp> cellsToRemove = new ArrayList<CellMoveUp>();
        
        for(int i = 0; i < movables.size(); i++){
           
            Movable movable = movables.get(i);
            if (movable == null)
                continue;
            int[] pos = movable.getMove();            
            handleWrap(pos);
            int newRow = pos[0];
            int newCol = pos[1];

            CellMoveUp movableCell = (CellMoveUp)movable;
            dish[movableCell.getCurrRow()][movableCell.getCurrCol()] = null;

            

            if(dish[newRow][newCol] == null) {
                dish[newRow][newCol] = movableCell.newCellCopy();          
            }
            else if(!(dish[newRow][newCol] instanceof Movable)) {
                if(dish[newRow][newCol] instanceof Divisible) {
                    divisibles.remove(dish[newRow][newCol]);
                } 
                
                dish[newRow][newCol].apoptosis();
                dish[newRow][newCol] = movableCell.newCellCopy();
            }
            else if(movableCell.compareTo(dish[newRow][newCol]) > 0) {
                //movables.set(movables.indexOf(dish[newRow][newCol]), null);
                cellsToRemove.add((CellMoveUp)dish[newRow][newCol]);
                dish[newRow][newCol].apoptosis();
                dish[newRow][newCol] = movableCell.newCellCopy();
            }
            else if(movableCell.compareTo(dish[newRow][newCol]) == 0) {
                //movables.set(movables.indexOf(movableCell), null);
                cellsToRemove.add((CellMoveUp)dish[newRow][newCol]);
                cellsToRemove.add((CellMoveUp)movableCell);
                //movables.set(movables.indexOf(dish[newRow][newCol]), null);
                dish[newRow][newCol].apoptosis();
                movableCell.apoptosis();
                dish[newRow][newCol] = null;
            }
            else {                
                //movables.set(movables.indexOf(movableCell), null);
                cellsToRemove.add((CellMoveUp)movableCell);
                movableCell.apoptosis();
            }

        }

        /*
        for (int i = 0; i < movables.size(); i++) {
            if(movables.get(i) == null)
                movables.remove(i);
        }
        
        for (int i = 0; i < divisibles.size(); i++) {
            if(divisibles.get(i) == null)
                divisibles.remove(i);
        }
        */

        for(int i = 0; i < cellsToRemove.size(); i++) {
            movables.remove(cellsToRemove.get(i));
        }
        
    }


    /**
     * Function used to divide every divisible cell on the petridish
     */
    public void divide() {

        Cell[][] tempDish = new Cell[dish.length][dish[0].length];
        
        int divsSize = divisibles.size();

        for(int i = 0; i < divsSize; i++){
           
            Divisible divisible = divisibles.get(i);
            if (divisible == null)
                continue;
            int[] pos = divisible.getDivision();            
            handleWrap(pos);
            int newRow = pos[0];
            int newCol = pos[1];
        

            CellDivide divisibleCell = (CellDivide)divisible;
                       
        
            if(dish[newRow][newCol] == null && tempDish[newRow][newCol] == null) {
                tempDish[newRow][newCol] = divisibleCell.newCellCopy(); 
                divisibles.add((Divisible)divisibleCell.newCellCopy()); 
            }

            
            else if(dish[newRow][newCol] == null && 
                    (tempDish[newRow][newCol] instanceof Divisible)) {
                if(divisibleCell.compareTo(tempDish[newRow][newCol]) > 0) {
                    tempDish[newRow][newCol].apoptosis();
                    tempDish[newRow][newCol] = divisibleCell.newCellCopy();
                }
                else if(divisibleCell.compareTo(tempDish[newRow][newCol])
                         == 0) {
                    tempDish[newRow][newCol].apoptosis();
                    divisibleCell.apoptosis();
                    tempDish[newRow][newCol] = null;
                }
                else {
                    divisibleCell.apoptosis();
                }
            }
            
        }
        for (int i = 0; i < dish.length; i++) {
            for (int j = 0; j < dish[0].length; j++) {
                if (tempDish[i][j] != null) {
                    dish[i][j] = tempDish[i][j].newCellCopy();
                }
            }
        }

    }

    /**
     * Kills all the cells that should be dead and also repopulates the dish
     * with new cells simulataneouly 
     */
    public void update() {
        //temporary dish to store cells to add to dish
        Cell[][] tempDish = new Cell[dish.length][dish[0].length];
        for (int i = 0; i < dish.length; i++) {
            for (int j = 0; j < dish[0].length; j++) { 
                if (getNeighborsOf(i,j).size() == 2 || 
                        getNeighborsOf(i,j).size() == 3) {                    
                    tempDish[i][j] = getNeighborsOf(i, j).get(0);
                }
            }
        }

        //temporary dish2 to store the cells that need to be removed from dish
        Cell[][] tempDish2 = new Cell[dish.length][dish[0].length];
        for (int i = 0; i < dish.length; i++) {
            for (int j = 0; j < dish[0].length; j++) {
                if(dish[i][j] != null && 
                        dish[i][j].checkApoptosis(getNeighborsOf(i,j))) {
                    
                    tempDish2[i][j] = dish[i][j];
                    
                }
            }
        }

        //uses both tempdish and tempdish2 to update the dish
        for (int i = 0; i < dish.length; i++) {
            for (int j = 0; j < dish[0].length; j++) {
                if (tempDish[i][j] != null && dish[i][j] == null) {
                    dish[i][j] = tempDish[i][j].newCellCopy();
                    if(tempDish[i][j] instanceof Movable)
                        movables.add((Movable)tempDish[i][j]);  
                    else if(tempDish[i][j] instanceof Divisible)
                        divisibles.add((Divisible)tempDish[i][j]);
                }
                if (tempDish2[i][j] != null) {
                    
                    if(tempDish2[i][j] instanceof Movable) {
                        System.out.println(tempDish2[i][j].toString());
                        //System.out.println(tempDish2[i][j].getCurrCol());
                        //System.out.println(tempDish2[i][j].getCurrRow());
                        movables.remove(tempDish2[i][j]); 
                        
                    }
                        
                    else if(tempDish2[i][j] instanceof Divisible) {
                        divisibles.remove((Divisible)tempDish2[i][j]);
                    }
                                            
                    dish[i][j] = null;
                }
            }
        }
        

    }

    /**
     * Function which calls the functions move, divide, and update once
     */
    public void iterate() {
        move();
        divide();
        update();
    }


  


    //This is the main driver of the program
    public static void main(String[] args) {

       
        String[][] petri = new String[][]{ {"null", "null", "null", "null", "null"}, 
                {"null", "CellStationary 2", "CellDivide 5", "CellStationary 11", "null"},
                {"null", "CellMoveDiagonal 4", "CellMoveToggle 3", "CellMoveToggle 10", "CellStationary 4"},
                {"null", "null", "CellDivide 1", "CellMoveUp 4", "null"} };
        
        /*
        String[][] petri = new String[][]{ {"null", "CellDivide 1", "null", "null"}, 
        {"null", "CellStationary 2", "CellDivide 5", "null"},
        {"null", "CellMoveDiagonal 4", "CellMoveToggle 3", "CellStationary 2"}, 
        {"CellMoveToggle 3", "null", "null", "CellStationary 5"} };
        


        String[][] petri = new String[][]{ {"CellMoveUp 0", "CellMoveToggle 1", "CellMoveToggleChild 2", "null"},
                                            {"CellMoveDiagonal 3", "CellDivide 4", "CellMoveToggle 5", "null"} };
        
        String[][] petri = new String[][]{ 
                {"null", "CellMoveDiagonal 4", "CellMoveToggle 3", "CellMoveToggle 10", "CellStationary 4"},
                {"null", "null", "CellDivide 1", "CellMoveUp 4", "null"},         
                {"null", "CellDivide 1", "null", "null", "null"}, 
                {"null", "CellStationary 2", "CellDivide 5", "null", "null"},
                {"null", "CellMoveDiagonal 4", "CellMoveToggle 3", "CellStationary 2", "null"},  };
        

        String[][] petri = new String[][]{ {"null", "CellStationary 1", "CellStationary 1", "CellStationary 1"}, 
        {"null", "CellStationary 2", "CellDivide 5", "CellStationary 1"},
        {"null", "CellStationary 4", "CellStationary 3", "CellStationary 2"}, 
        {"CellMoveToggle 3", "CellStationary 1", "CellStationary 1", "CellStationary 5"} };
        */

        PetriDish newDish = new PetriDish(petri);
        System.out.println("Divisibles size: " + newDish.divisibles.size());
        System.out.println(newDish.toString());

        
        //System.out.println(newDish.getNeighborsOf(0,3));
        newDish.move();
        System.out.println(newDish.toString());
        System.out.println("Movables: " + newDish.movables);
        System.out.println("Divisibles: " + newDish.divisibles);

        //newDish.move();
        //System.out.println(newDish.toString());
        //System.out.println("Movables: " + newDish.movables);
        //System.out.println("Divisibles: " + newDish.divisibles);
        System.out.println("Divisibles size: " + newDish.divisibles.size());
        newDish.divide();
        System.out.println(newDish.toString());
        System.out.println("Movables: " + newDish.movables);
        System.out.println("Divisibles: " + newDish.divisibles);
        //newDish.divide();
        //System.out.println(newDish.toString());
        newDish.update();
        System.out.println(newDish.toString());
        System.out.println("Movables: " + newDish.movables);
        System.out.println("Divisibles: " + newDish.divisibles);
        
        //newDish.iterate();
        //System.out.println(newDish.toString());
        //newDish.iterate();
        //System.out.println(newDish.toString());
        
    }
    

}
