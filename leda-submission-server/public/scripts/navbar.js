export default class Navbar {
    init(container){
        this.container = container;
        this.render();        
    }

    render() {
        this.container.innerHTML = Navbar.markup(this);
    }

    static markup({}){
        return `
            <h1>Test, Dorits!</h1>
        `
    }

    constructor(container) {
        if (typeof container.dataset.ref === 'undefined') {
            this.ref = Math.random();
            Navbar.refs[this.ref] = this;
            container.dataset.ref = this.ref;
            this.init(container);
        } else {
            return Navbar.refs[container.dataset.ref];
        }
    }

}

Navbar.refs = {};

document.addEventListener('DomContentLoaded', () => {
    new Navbar(document.querySelector('#test'))
})