var mysql = require("mysql");
var connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "furniture",
});
connection.connect(function (err) {
  if (!err) {
    console.log("Database is connected ... nn");
  } else {
    console.log("Error connecting database ... nn");
  }
});

exports.register = async function (req, res) {
  console.log("Register Api Called: ");
  var user_email = req.body.user_email;
  var user_password = req.body.user_password;

  // const encryptedPassword = await bcrypt.hash(user_password, saltRounds)
  var users = {
    user_email: user_email,
    user_password: user_password,
  };

  connection.query(
    "SELECT COUNT(*) AS cnt FROM tbluser WHERE user_email = ?",
    [user_email],
    async function (error, results, fields) {
      if (error) {
        res.json({
          code: 400,
          failed: "error ocurred",
        });
      } else {
        console.log("Here is register results: ", results.user_email);

        if (results[0].cnt > 0) {
          res.json({
            code: 202,
            success: "User already exist",
          });
        } else {
          connection.query("INSERT INTO tbluser SET ?", users, async function (
            error,
            results,
            fields
          ) {
            if (error) {
              res.json({
                code: 400,
                failed: "error ocurred",
              });
            } else {
              res.json({
                code: 200,
                success: "user registered sucessfully",
              });
            }
          });
        }
      }
    }
  );
};

exports.login = async function (req, res) {
  console.log("Login Api Called: ");
  var user_email = req.body.user_email;
  var user_password = req.body.user_password;
  connection.query(
    "SELECT * FROM tbluser WHERE user_email = ?",
    [user_email],
    async function (error, results, fields) {
      if (error) {
        res.json({
          code: 400,
          failed: "error ocurred",
        });
      } else {
        if (results.length > 0) {
          // const comparision = await bcrypt.compare(user_password, results[0].password)
          if (user_password == results[0].user_password) {
            res.json({
              code: 200,
              success: "login sucessfull",
              data: results[0],
            });
          } else {
            res.json({
              code: 204,
              success: "Email and password does not match",
            });
          }
        } else {
          res.json({
            code: 206,
            success: "Email does not exits",
          });
        }
      }
    }
  );
};
