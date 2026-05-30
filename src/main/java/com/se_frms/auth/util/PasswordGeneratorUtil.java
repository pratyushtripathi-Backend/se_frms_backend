package com.se_frms.auth.util;


import java.security.SecureRandom;

public final class PasswordGeneratorUtil {

    private static final String UPPERCASE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWERCASE =
            "abcdefghijklmnopqrstuvwxyz";

    private static final String NUMBERS =
            "0123456789";

    private static final String SPECIAL_CHARACTERS =
            "@#$%!&*";

    private static final String ALL_CHARACTERS =
            UPPERCASE
                    + LOWERCASE
                    + NUMBERS
                    + SPECIAL_CHARACTERS;

    private static final int PASSWORD_LENGTH = 14;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private PasswordGeneratorUtil() {

        throw new IllegalStateException(
                "Utility class"
        );
    }

    public static String generateSecurePassword() {

        StringBuilder password =
                new StringBuilder(PASSWORD_LENGTH);

        password.append(
                getRandomCharacter(UPPERCASE)
        );

        password.append(
                getRandomCharacter(LOWERCASE)
        );

        password.append(
                getRandomCharacter(NUMBERS)
        );

        password.append(
                getRandomCharacter(SPECIAL_CHARACTERS)
        );

        for (int i = 4; i < PASSWORD_LENGTH; i++) {

            password.append(
                    getRandomCharacter(ALL_CHARACTERS)
            );
        }

        return shufflePassword(
                password.toString()
        );
    }

    private static char getRandomCharacter(
            String characters
    ) {

        return characters.charAt(
                SECURE_RANDOM.nextInt(
                        characters.length()
                )
        );
    }

    private static String shufflePassword(
            String password
    ) {

        char[] characters =
                password.toCharArray();

        for (int i = 0; i < characters.length; i++) {

            int randomIndex =
                    SECURE_RANDOM.nextInt(
                            characters.length
                    );

            char temp = characters[i];

            characters[i] =
                    characters[randomIndex];

            characters[randomIndex] = temp;
        }

        return new String(characters);
    }
}

