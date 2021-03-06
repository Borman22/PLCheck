// В этом классе хранятся списки всех программ
package playlist;

public class AllPrograms {

    // Все программы, на которые есть склеротики
    public static String[][] progNameAndDali = {
            {"Vtoraya", "Dali HS-Druge Jittya"},
            //{"Vacances", "Dali HS-Les Vacances de iAmour"},
//            {"Mister", "Мистер Бин", "Dali HS-Mister Bin"},
//            {"Ogon", "Dali HS-Ogon Lyubvi"},
            //{"Diabola", "Dali HS-Santa Diabla"},
            //{"Numo", "Dali MS-321 Numo"},
            //{"Ernie", "Dali MS-Bert and Ernie"},
            //{"Elmo", "Dali MS-Elmo"},
            //{"Graysya", "Dali MS-Graysya"},
            //{"Grover", "Dali MS-Grover"},
            //{"Redyska", "Редиска", "Dali MS-Rediska"},
            {"3x4", "Зх4", "3х4", "Dali PR-3x4"},
//            {"Amerika", "Америка имеет талант", "Dali PR-Amerika Mae Talant"},
//            {"Anatomiya", "Анатомия славы", "Dali PR-Anatomiya Slavy"},
//            {"Pozhenimsya", "Давай одружимось", "Dali PR-Davay Pozhenimsya"}, //
            {"Enyky", "Эники", "Еники", "Dali PR-Enyky Benyky"},
//            {"GLUPERS", "Глуперсы", "Dali PR-Glupers"},
            {"Hovanky", "семейные прятки", "Dali PR-Hovanky"},
            {"ROZVODI", "Rozvodi", "Rozvody", "Крутые разводы", "Круті розводи", "Dali PR-Kruti Rozvodi"}, //
//            {"Lolita", "Dali PR-Lolita"},
//            {"More", "Море по колено", "MK_", "Dali PR-More po kolino"},
            {"Jamies 15", "Jamies15", "Jamie15", "Jamies_15", "Обед за 15 минут", "Dali PR-Oliver 15min"},
            {"za 30 min", "Обед за 30 минут", "Jamies30M", "Jamie30", "JO30MM", "Dali PR-Oliver 30min"},
            {"Jamies_Big_Festival", "Кормчий Джейми", "Харчевой Джейми", "Dali PR-Oliver Festival"},
            {"Jamies Great Britain", "Британская кухня", "Dali PR-Oliver Great Britain"},
            {"Jamies_American_Road_Trip", "Кулинарные путешествия", "Dali PR-Oliver Mandrivki"},
            {"_Twist", "Oliverstwist", "Рецепты Джейми", "Dali PR-Oliver Recepty"},
            {"Paral", "Паралельний світ", "Параллельный мир", "Dali PR-Paralelniy Svit"},
            {"Rozkishne", "Dali PR-Rozkishne zhittya"},
            {"SimeyniPrystrasti", "Semeynye_strasti", "Dali PR-Simeyni Pristrasti"}, // 6???
            {"Virus", "Dali PR-Virus Smihu"},
            {"Vuso", "Вусолапохвіст", "УсоЛапоХвост", "Dali PR-Vusolapohvist"}, //
//            {"groshi", "деньги", "Dali PR-Za groshi"},
//            {"MS-Bernard", "Dali MS-Bernard"},
//            {"MS-Fixies", "Dali MS-Fixies"},
//            {"MS-Juki", "Жуки", "Dali MS-Juki"},
//            {"MS-Monk", "Монк", "Dali MS-Monk"},
//            {"PR-Moment-skalpelya", "Dali PR-Moment-skalpelya"},
            {"PR-Bytva", "Dali PR-Bytva"},
//            {"BeetParty", "Свеколки", "Dali MS-BeetParty"},
//            {"PR-Zhit-zdorovo", "Жить здорово", "Dali PR-Zhit zdorovo"},
            {"HS-SEX-MISTO", "Секс и город", "Секс і місто", "Dali HS-Sex i Misto"},//
            {"Медиум", "Медіум", "HS-Medium", "Dali HS-Medium"}, //
            {"HS-CSI-S", "Місце злочину Лас-Вегас", "Dali HS-CSI"}, //
            {"Морская полиция", "Морська поліція", "HS-NCIS", "Dali HS-NCIS"}, //
            {"CriminalMinds", "Мислити як вбивця", "Dali HS-CriminalMinds"}, //
            //{"PR-Kroliki", ""}
            //{"Карамба", "Dali PR-Karamba"}  // Программу не нашел
            {"HS-Deathinparadise", "Dali HS-Deathinparadise"},
            {"HS-Callthemidwife", "Dali HS-Callthemidwife"},
            {"HS-CSI-NEW-York", "Место преступления: Нью-Йорк", "Dali HS-CSI NewYork"},
            {"HS-CHAU", "Чисто английские", "Dali HS-CHAU"},
            {"HS-Hawaii", "Dali HS-Hawaii"}
    };


    //Мультики, на которые надо ставить знак круг
    public static String[] multsName = {"Numo", "Ernie", "Elmo", "Graysya", "Grover", "Redyska", "Редиска", "BeetParty", "Свеколки", "MS-Bernard", "MS-Fixies-Aeroplan", "MS-Juki", "Жуки", "MS-Monk", "Монк"};
    //Программы, на которые нет склеротиков (для того, чтобы программа не ругалась, что склеротики не установлены)
    public static String[] withoutDali = {"HF-"};

    public static String[] dynamicScleroticBudni = {/*"MS-Fixies everyday 0730",     "PR-Moment-skalpelya today 1900",   */"PR-Bytva today 2000"};
    public static String[] dynamicScleroticVihodnoj = {/*"MS-Fixies everyday 0730",     "PR-Moment-skalpelya today 1900",   */"PR-Bytva today 2100"};
    public static String[] dynamicScleroticDali = {/*"Dali MS-Fixies",              "Dali PR-Moment-skalpelya",         */"Dali PR-Bytva"};
    public static int[] startOfDynamicScleroticBudni = {/*810000,                        810000,                             */810000}; //9.00  9.00
    public static int[] stopOfDynamicScleroticBudni = {/*1710000,                       1710000,                            */1800000}; //19.00 20.00
    public static int[] startOfDynamicScleroticVihodnoj = {/*810000,                        810000,                             */810000}; //9.00  9.00
    public static int[] stopOfDynamicScleroticVihodnoj = {/*1710000,                       1710000,                            */1890000}; // 19.00 21.00

    // Эротика
    public static String[] erotica = {"HF-Corporate", "HF-Delilah", "HF-Fit", "HF-Housemates", "HF-Lena", "HF-Pearl", "HF-Pleasure-", "HF-Riot", "HF-Sexology", "HF-Taste", "HF-Tease", "HF-The-Invitation", "HF-The-Matchmaker"};








}
