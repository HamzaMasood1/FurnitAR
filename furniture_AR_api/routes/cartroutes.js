
var mysql = require('mysql');
var connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'furniture'
});
connection.connect(function (err) {
    if (!err) {
        console.log("Database is connected ... nn");
    } else {
        console.log("Error connecting database ... nn");
    }
});
exports.addToCart = async function (req, res) {

    console.log('addToCart Api Called: ');
    var quantity = req.body.quantity;
    var id = req.body.id;
    var name = req.body.name;
    var price = req.body.price;
    var user_id = req.body.user_id;
    var _quantity = req.body._quantity;

    connection.query('UPDATE tblitems SET quantity = ? WHERE id = ?', [quantity, id], async function (error, results, fields) {
        if (error) {
            res.json({
                "code": 400,
                "failed": error
            })
        } else {

            connection.query('SELECT * FROM tblcart WHERE item_id = ?', [id], async function (error, results, fields) {
                if (error) {
                    res.json({
                        "code": 400,
                        "failed": error
                    });
                } else {
                    
                    if (results[0] != null) {
                        const db_q = results[0].quantity;
                        connection.query('UPDATE tblcart SET quantity = ? WHERE item_id = ?', [db_q + _quantity, id], async function (error, results, fields) {

                            if (error) {
                                res.json({
                                    "code": 400,
                                    "failed": error
                                });
                            } else {
                                res.json({
                                    "code": 200,
                                    "success": "",
                                });
                            }

                        });

                    } else {
                        var cart = {
                            "item_id": id,
                            "user_id": user_id,
                            "name": name,
                            "price": price,
                            "quantity": _quantity,
                        }
    
                        connection.query('INSERT INTO tblcart SET ?', cart, async function (error, results, fields) {
    
                            if (error) {
                                res.json({
                                    "code": 400,
                                    "failed": error
                                });
                            } else {
                                res.json({
                                    "code": 200,
                                    "success": "",
                                });
                            }
    
                        });
                    }
                }
            });
        }
    });
}

exports.getCarts = async function (req, res) {

    console.log('getCarts Api Called: ');
    var user_id = req.body.user_id;
    connection.query('SELECT * from tblcart WHERE user_id = ?', [user_id], async function (error, results, fields) {
        if (error) {
            res.json({
                "code": 400,
                "failed": "error ocurred"
            })
        } else {
            var response_data = results;

            res.json({
                "code": 200, 
                "success": "",
                "data": response_data
            });
        }
    });
}

exports.removeFromCart = async function (req, res) {

    console.log('removeFromCart Api Called: ');
    var quantity = req.body.quantity;
    var id = req.body.id;
    connection.query('SELECT * FROM tblcart WHERE id = ?', [id], async function (error, results, fields) {
        if (error) {
            res.json({
                "code": 400,
                "failed": "error ocurred"
            })
        } else {

            var response_data = results;
            connection.query('DELETE FROM tblcart WHERE id = ?', [id], async function (error, results, fields) {
                if (error) {
                    res.json({
                        "code": 400,
                        "failed": "error ocurred"
                    })
                } else {
                    
        
                    res.json({
                        "code": 200, 
                        "success": "",
                        "data": response_data
                    });
                }
            });
        }
    });
    
}

exports.clearCart = async function (req, res) {

    console.log('clearCart Api Called: ');
    connection.query('DELETE FROM tblcart', 0, async function (error, results, fields) {
        if (error) {
            res.json({
                "code": 400,
                "failed": "error ocurred"
            })
        } else {

            res.json({
                "code": 200, 
                "success": "successfully deleted",
            });
        }
    });
}