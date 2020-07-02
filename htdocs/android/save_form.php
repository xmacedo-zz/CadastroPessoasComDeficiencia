<?php
error_reporting (0);
if ($_SERVER['REQUEST_METHOD']=='POST'){

    require_once 'connect.php';

    $nome         = $_POST['nome'];
    $email        = $_POST['email'];
    $nascimento   = $_POST['nascimento'];
    $telefone     = $_POST['telefone'];
    $detalhes     = $_POST['detalhes'];
    $endereco     = $_POST['endereco'];
    $cidade       = $_POST['cidade'];
    $cep          = $_POST['cep'];
    $sexo         = $_POST['sexo'];
    $tipos_def    = $_POST['tipos_def'];

    $sql          = "INSERT INTO form_pessoas_def(nome, email, data_nascimento, telefone, detalhes, endereco, cidade, cep, sexo, tipos_deficiencia ) VALUES ('$nome','$email','$nascimento','$telefone','$detalhes','$endereco','$cidade','$cep','$sexo','$tipos_def')";

    if(mysqli_query($conn, $sql)){
        $result["sucess"] = "1";
        $result["message"] = "sucess";
    }else{
        $result["sucess"] = "0";
        $result["message"] = "error";
    }
    echo json_encode($result);
    mysqli_close($conn);
}

?>
