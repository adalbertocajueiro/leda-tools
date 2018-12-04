class UserNav extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        this.innerHTML = 
        `<nav class="navbar header-top fixed-top navbar-expand-lg  navbar-dark bg-dark">
            <span class="navbar-toggler-icon leftmenutrigger"></span>
            <a class="navbar-brand" href="#">LEDA UFCG</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText"
                aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
    
                <ul class="navbar-nav animate side-nav">
                    <li>
                        <img id="user-img">
                    </li>
                    <li id="login" class="nav-item">
                        <a class="nav-link" id="my-signin2">Login</a>
                    </li>
                    <li id="logout" class="nav-item" style="display:none;">
                        <a class="nav-link user-link" onclick="signOut()">Logout</a>
                    </li>
                </ul>
                <ul class="navbar-nav ml-md-auto d-md-flex">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="alunos">Estudantes</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="cronograma">Cronograma</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="config">Configurações</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true"
                            aria-expanded="false">Participação <b class="caret"></b></a> 
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="submissoes?showAll=false">Submissões</a>
                            <a class="dropdown-item" href="notas">Notas</a>
                            <a class="dropdown-item" href="faltas">Faltas</a>
                        </div>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="roteirosEspeciais">Roteiros Especiais</a>
                    </li>
                </ul>
            </div>
        </nav>`;
    }
}

try {
    customElements.define('user-nav', UserNav)
} catch (err) {
    const h3 = document.createElement('h3');
    h3.innerHTML = "Something went wrong!";
    document.body.appendChild(h3);
}