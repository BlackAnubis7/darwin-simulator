# Symulator ewolucyjny
Poniższy plik jest krótką instrukcją obsługi Symulatora Ewolucyjnego

## Uruchamianie programu
Klasą która zawiera metodę `main()` i powinna być uruchomiona jest `MainEngine`. W konsoli wyświetlają się seedy poszczególnych map. Każda z symulacji posiada osobne przyciski:
- `[Start]/[Pause]` - wznawia bądź zatrzymuje symulację
- `[Step]` - natychmiast przechodzi do kolejnego dnia (dostępne przy zatrzymanej symulacji)
- `[Highlight]` - zaznacza zwierzęta, których dominującym genem jest gen globalnie dominujący
- `[Write to .txt]` - dopisuje pakiet danych do pliku wyjścia
- `[+] [Skip n days] [-]` - natychmiast pomija wyznaczoną liczbę dni

Dodatkowo w panelu sterowania znajduje się przycisk `[TOGGLE ALL]`, który zatrzymuje wszystkie uruchomione symulacje, a uruchamia wszystkie zatrzymane.

## Grafika
Kolory używane w symulacji ustalone są w klasie `Assets`. Pola stepów są bladozielone, a pola dżungli ciemnozielone. Gdy na polu jest roślina, staje się ono ciemniejsze. Zwierzęta oznaczone są kolorowymi kołami z odcinkiem w środku. Odcinek biegnie od środka koła, w stronę w którą zwrócone jest zwierzę. Kolor zwierzęcia reprezentuje jego energię. Kolor zielony oznacza energię większą od dwukrotności energii startowej, kolor żółty równą energii startowej, kolor czerwony energię bliską 0, a ciemnoczerwony oznacza, że zwierzę umrze jeśli w następnym ruchu nie zje rośliny. Wartości pośrednie są odpowiednio reprezentowane przez kolory pośrednie.

## Dane wejściowe
Dane wejściowe do programu zawarte są w pliku `parameters.json`, który znajduje się w głównym katalogu projektu. Zawarte są w nim następujące parametry:
- `"width"` - szerokość mapy w polach
- `"height"` - wysokość mapy w polach
- `"jungleWidth"` - szerokość dżungli w polach
- `"jungleHeight"` - wysokość dżungli w polach
- `"startAnimals"` - zwierzęta, które rodzą się w dniu 0
- `"startEnergy"` - energia z jaką zaczynają zwierzęta
- `"movementCost"` - koszt energetyczny ruchu
- `"plantEnergy"` - energia jakiej dostarczają rośliny
- `"numberOfMaps"` - liczba niezależnych symulacji (może być >2)
- `"sleepTime"` - czas między kolejnymi dniami (w milisekundach)
- `"fixedSeed"` - seed symulacji (wartość 0 oznacza losowego seeda)
- `"maxWindowWidth"` - maksymalna szerokość okna symulacji
- `"maxWindowHeight"` - maksymalna wysokość okna symulacji

## Eksport statystyk do pliku
Pliki wyjścia znajdują się w folderze `stats`, umieszczonym w głównym katalogu projektu. Każdy plik jest oznaczony sygnaturą czasową. Plik nie zostaje stworzony, jeśli żaden pakiet danych nie zostanie wyeksportowany. Każdy pakiet zawiera numer mapy, dzień eksportu oraz dane - osobno aktualne, osobno średnie dla całej symulacji do danego dnia.