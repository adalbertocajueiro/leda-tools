class Footer extends HTMLElement {
    
    constructor() {
        super();
    }

    connectedCallback() {
        this.render();
    }

    _getYear(){
        return new Date().getFullYear();
    }

    _setTemplate() {
        this.innerHTML = `
        <div class="container">
            <div class="footer-copyright font-small text-center py-3">
                Copyright ©
                ${this._getYear()} - Laboratório de Estruturas de Dados - 
                <a href="http://hericles.me" class="text-muted font-weight-light" target="_blank">Héricles Emanuel.</a>
            </div>
        </div>
        `
    }

    render() {
        this._setTemplate();
    }
}

try {
    customElements.define('leda-footer', Footer);
} catch (err) {
    const h3 = document.createElement('h3');
    h3.innerHTML = "Something went wrong!";
    document.body.appendChild(h3);
}