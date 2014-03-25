package KAT;

public class SpecialCharacterFactory
{
    public static SpecialCharacter createSpecialCharacter( String name ){
        SpecialCharacter specialCharacter = null;

        if( name.equals("Assassin Primus") ){
            specialCharacter = new AssassinPrimus();
        } // etc

        return specialCharacter;
    }
}
