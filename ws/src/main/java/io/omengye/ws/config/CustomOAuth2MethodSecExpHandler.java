package io.omengye.ws.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

import io.omengye.ws.common.base.Constants;

public class CustomOAuth2MethodSecExpHandler extends OAuth2MethodSecurityExpressionHandler{

	private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private String defauleRolePrefix=Constants.rolePrefix;
	
    // parent constructor
    public CustomOAuth2MethodSecExpHandler() {
        super();
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
    	
    	MySecurityExpressionRoot root = new MySecurityExpressionRoot(
                    authentication);
            root.setTrustResolver(trustResolver);
            root.setPermissionEvaluator(new CustomPermissionEvaluator());
            root.setRoleHierarchy(getRoleHierarchy());
            root.setDefaultRolePrefix(defauleRolePrefix);

            return root;
        }
	
}
