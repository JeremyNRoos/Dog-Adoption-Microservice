# Security Group for RDS
resource "aws_security_group" "rds" {
  name        = "${local.env}-${local.eks_name}-rds-sg"
  description = "Security group for RDS MySQL instance"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "MySQL from EKS nodes"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_eks_cluster.eks.vpc_config[0].cluster_security_group_id]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.env}-${local.eks_name}-rds-sg"
  }
}

# DB Subnet Group
resource "aws_db_subnet_group" "main" {
  name       = "${local.env}-${local.eks_name}-db-subnet-group"
  subnet_ids = [
    aws_subnet.private_zone1.id,
    aws_subnet.private_zone2.id
  ]

  tags = {
    Name = "${local.env}-${local.eks_name}-db-subnet-group"
  }
}

# RDS MySQL Instance
resource "aws_db_instance" "mysql_adopter" {
  identifier = "mysql1"
  
  engine               = "mysql"
  engine_version       = "5.7"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20
  storage_type         = "gp2"
  
  db_name  = "adopter_db"
  username = "admin"
  password = "12345678"  # Consider using AWS Secrets Manager for production
  
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  
  publicly_accessible = false
  skip_final_snapshot = true
  
  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "mon:04:00-mon:05:00"
  
  tags = {
    Name = "${local.env}-${local.eks_name}-mysql-adopter"
  }
}

# Output the RDS endpoint
output "rds_endpoint" {
  value       = aws_db_instance.mysql_adopter.endpoint
  description = "RDS MySQL endpoint"
}

output "rds_address" {
  value       = aws_db_instance.mysql_adopter.address
  description = "RDS MySQL address"
}