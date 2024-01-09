# Categories
Web version of categories word game


## 1. Gra będzie obsługiwała dwa tryby:
- Państwa: gracze będą podawać państwa.
- Miasta: gracze będą podawać miasta, które należą do danego państwa.
Tryb będzie losowany

## 2. Gra będzie premiowała czas i unikalność odpowiedzi.
Za szybszą poprawną odpowiedź gracz dostaje dodatkowe punkty. Przykładowo:
- <0,5> sekund - 10 punktów
- (5, 10> sekund - 5 punktów)
- (10, 15> sekund - 3 punkty)
## 3. Gracz może dołączyć w dowolnej chwili do gry.
## 4. Gra będzie obsługiwana przez interfejs graficzny.

# Opis funkcjonalności:

## 1. Logowanie:
- Gracz podaje swój login.
- Jeśli login jest zajęty, gracz musi podać inny.

## 2. Gra:
- Gracz podaje odpowiedź na pytanie.
- Jeśli odpowiedź jest poprawna, gracz dostaje punkty.
- Jeśli odpowiedź jest unikalna, gracz dostaje dodatkowe punkty.
Gra będzie składała się z kilku rund (podany w kodzie jako stała/parametr) albo do momentu kiedy wyjdą wszyscy gracze albo zostanie jeden.

### Parametry konfiguracyjne:
1. Czas na odpowiedź na pytanie.
2. Liczba punktów za poprawną odpowiedź.
3. Liczba punktów za unikalną odpowiedź.

Gra zaczyna się w momencie, kiedy do gry dołączy przynajmniej dwóch graczy i przynajmniej 51% z graczy w lobby potwierdzi gotowość do gry.

### Przed grą:
- Gracze widzą liczbę pozostałych graczy i mogą potwierdzić swoją gotowość do gry.

### W trakcie gry:
- Gracze widzą kategorię (np. Państwa) i muszą podać konkretne Państwo rozpoczynające się na podaną literę (np. A).
- Gracze widzą swoje punkty.

### Po skończeniu gry:
- Gracze widzą swoje punkty.
- Gracze widzą ranking graczy.

Gracze dołączający do trwającej gry widzą ekran oczekiwania
i po zakończeniu aktualnej rundy (zakończeniu aktualnego pytania)
dołączają aktywnie do gry i mogą odpowiadać na kolejne pytania
