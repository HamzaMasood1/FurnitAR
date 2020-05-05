var express = require("express");
var login = require('./routes/loginroutes');
var data = require('./routes/dataroutes');
var cart = require('./routes/cartroutes');
var bodyParser = require('body-parser');

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});
var router = express.Router();
// test route
router.get('/', function(req, res) {
    console.log('Test Success');
    res.json({ message: 'welcome to our upload module apis' });
});
//route to handle user registration
router.post('/register',login.register);
router.post('/login',login.login);
router.post('/data', data.data);
router.post('/update', data.update);
router.post('/addcart', cart.addToCart);
router.post('/getcarts', cart.getCarts);
router.post('/removefromcart', cart.removeFromCart);
router.post('/clearcart', cart.clearCart);
router.post('/searchdata', data.searchdata);
app.use('/api', router);
app.listen(3000);