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
exports.data = async function (req, res) {

    console.log('data Api Called: ');
    // var quantity = req.body.quantity;
    // var user_password = req.body.user_password;
    connection.query('SELECT * FROM tblitems WHERE quantity > ?', 0, async function (error, results, fields) {
        if (error) {
            res.json({
                "code": 400,
                "failed": "error ocurred"
            })
        } else {
            // if (results.length > 0) {
            //     // const comparision = await bcrypt.compare(user_password, results[0].password)
            //     if (user_password == results[0].user_password) {
            //         res.json({
            //             "code": 200,
            //             "success": "login sucessfull"
            //         })
            //     }
            //     else {
            //         res.json({
            //             "code": 204,
            //             "success": "Email and password does not match"
            //         })
            //     }
            // }
            // else {
            //     res.json({
            //         "code": 206,
            //         "success": "Email does not exits"
            //     });
            // }

            var response_data = results;

            res.json({
                "code": 200, 
                "success": "",
                "data": response_data
            });
        }
    });
}

exports.update = async function (req, res) {

    console.log('update Api Called: ');
    var quantity = req.body.quantity;
    var id = req.body.id;
    connection.query('UPDATE tblitems SET quantity = ? WHERE id = ?', [quantity, id], async function (error, results, fields) {
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

exports.searchdata = async function (req, res) {

    console.log('searchdata Api Called: ');
    var searchquary = req.body.searchquary;
    var s = req.body.s;
    connection.query('SELECT * FROM tblitems WHERE name >= ? and name < ?', [searchquary, s], async function (error, results, fields) {
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

