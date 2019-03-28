![TrackNMe](https://www.tracknme.com.br/app/images/logo-tracknme.png)


# Desafio Mobile

O candidato deve dar **fork** neste repositório e após o termino do desenvolvimento, realizar um **pull request** e avisar por **email** para análise do time.

O desafio consiste em criar um app com as seguintes características:

# Task 1 - Desenhar um trajeto no mapa

Implementar um app cuja tela principal apresenta um mapa (google maps) e um trajeto obtido de uma api REST. Você deve implementar um endpoint REST e disponibiliza-lo remotamente para que seja consumido pelo app.
O app deve consultar a api REST e salvar o resultado (exemplo em '/assets/posicoes.json') em banco de dados local.
Após a consulta o app deve fazer uma consulta a base de dados local e desenhar o trajeto obtido da base local.

# Task 2 - Calendario

Nesta fase, o app deve ser capaz de fazer uma consulta ao serviço REST passando uma data como parametro e mostrando o trajeto conforme a data informada.

Ex.

GET /posicoes?data=2017-01-01 (Imprime apenas as posições obtidas do dia 01/01/2017).

GET /posicoes?data=2017-01-02 (Imprime apenas as posições obtidas do dia 02/01/2017).

E assim por diante.

Todos os resultados devem ser salvos em banco de dados local.
Para efeito de cache, posições de uma data já pesquisada, não devem fazer a chamada REST novamente. Seu conteudo deve ser obtido do banco de dados local, neste caso.

---
#### LICENSE
```
MIT License

Copyright (c) 2017 TrackNMe

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
