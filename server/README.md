Server side of the game

Done in C++

Proces 1: Login

Klient wysyła komunikat w postaci Login:"<...>"
Serwer sprawdza czy login jest wolny i zwraca kominkat do klienta Login:OK/LOGIN:NO
Jeżeli logowanie się powiodło klient dołącza do poczekalni i ma możliwość wysłania komunikatu potwierdzającego gotowość READY

Proces 2: Rozgrywka
Serwer wysyła klientowi komunikat z kategorią i literą
Klient wysyła odpowiedź ANS:"<...>"
Jeżeli odpowiedź zaczyna się na odpowiednią literę i znajduje się w pliku z odpowiedziami to odpowiedź jest uznawana i przyznawane są punkty
Po zakończeniu rundy serwer zwraca do klienta informację o jego punktach
