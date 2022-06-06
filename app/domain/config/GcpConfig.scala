package domain.config

import com.google.auth.Credentials

case class GcpConfig(credentials: Credentials, project: String)
